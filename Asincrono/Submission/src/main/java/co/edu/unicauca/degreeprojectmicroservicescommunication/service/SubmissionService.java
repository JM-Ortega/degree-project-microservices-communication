package co.edu.unicauca.degreeprojectmicroservicescommunication.service;

import co.edu.unicauca.degreeprojectmicroservicescommunication.infra.dto.AnteproyectoMessage;
import co.edu.unicauca.degreeprojectmicroservicescommunication.infra.dto.AnteproyectoRequest;
import co.edu.unicauca.degreeprojectmicroservicescommunication.entity.Anteproyecto;
import co.edu.unicauca.degreeprojectmicroservicescommunication.entity.Docente;
import co.edu.unicauca.degreeprojectmicroservicescommunication.entity.Estudiante;
import co.edu.unicauca.degreeprojectmicroservicescommunication.entity.TrabajoDeGrado;
import co.edu.unicauca.degreeprojectmicroservicescommunication.repository.AnteproyectoRepository;
import co.edu.unicauca.degreeprojectmicroservicescommunication.repository.DocenteRepository;
import co.edu.unicauca.degreeprojectmicroservicescommunication.repository.EstudianteRepository;
import co.edu.unicauca.degreeprojectmicroservicescommunication.repository.TrabajoDeGradoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio encargado de manejar la lógica de negocio relacionada con los anteproyectos.
 *
 * <p>Esta clase se encarga de crear y persistir anteproyectos en la base de datos,
 * así como de enviar los datos correspondientes a través de RabbitMQ para su procesamiento
 * por otros microservicios.</p>
 *
 * <p>El método principal {@link #crearAnteproyecto(AnteproyectoRequest)} realiza el
 * mapeo entre las entidades del dominio y el mensaje DTO antes de enviarlo.</p>
 */
@Service
public class SubmissionService {
    /** Repositorio para la persistencia de entidades {@link Anteproyecto}. */
    @Autowired
    private AnteproyectoRepository anteproyectoRepository;

    @Autowired
    private EstudianteRepository estudianteRepository;

    @Autowired
    private DocenteRepository docenteRepository;

    @Autowired
    private TrabajoDeGradoRepository trabajoRepository;

    /** Componente responsable de enviar mensajes a RabbitMQ. */
    @Autowired
    private RabbitMQSender rabbitMQSender;

    /**
     * Crea un nuevo anteproyecto a partir de los datos proporcionados,
     * lo guarda en la base de datos y envía un mensaje con su información
     * a través de RabbitMQ.
     *
     * <p>Durante el proceso:
     * <ul>
     *   <li>Se construye la entidad {@link Anteproyecto} con su respectivo {@link TrabajoDeGrado}.</li>
     *   <li>Se mapean los estudiantes, el director y opcionalmente el codirector.</li>
     *   <li>Se guarda el anteproyecto en el repositorio.</li>
     *   <li>Se crea y envía un objeto {@link AnteproyectoMessage} al exchange configurado en RabbitMQ.</li>
     * </ul>
     * </p>
     *
     * @param request objeto con los datos necesarios para crear el anteproyecto.
     * @return el objeto {@link Anteproyecto} guardado en la base de datos.
     */
    public Anteproyecto crearAnteproyecto(AnteproyectoRequest request) {
        Anteproyecto anteproyecto = new Anteproyecto();
        anteproyecto.setTitulo(request.getTitulo());
        anteproyecto.setDescripcion(request.getDescripcion());
        anteproyecto.setFechaCreacion(new Date());

        TrabajoDeGrado trabajo = new TrabajoDeGrado();

        List<Estudiante> estudiantes = request.getEstudiantes()
                .stream()
                .map(dto -> estudianteRepository.findByCorreo(dto.getCorreo())
                        .orElseGet(() -> new Estudiante(dto.getNombre(), dto.getCorreo(), dto.getCodigo())))
                .collect(Collectors.toList());

        // Validación: cada estudiante solo puede tener 1 trabajo de grado
        for (Estudiante e : estudiantes) {
            List<TrabajoDeGrado> trabajosExistentes = estudianteRepository.findTrabajosByEstudianteCorreo(e.getCorreo());
            if (!trabajosExistentes.isEmpty()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "El estudiante " + e.getNombre() + " ya está asociado a un trabajo de grado."
                );
            }
        }

        trabajo.setEstudiantes(estudiantes);

        Docente director = docenteRepository.findByCorreo(request.getDirector().getCorreo())
                .orElseGet(() -> new Docente(
                        request.getDirector().getNombre(),
                        request.getDirector().getCorreo(),
                        request.getDirector().getDepartamento()
                ));

        // Validación: un profesor no puede tener más de 3 trabajos de grado
        Long trabajosDirector = trabajoRepository.countTrabajosByProfesor(director.getCorreo());
        if (trabajosDirector >= 3) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El director " + director.getNombre() + " ya tiene 3 trabajos de grado."
            );
        }

        trabajo.setDirector(director);

        Docente codirector = null;
        if (request.getCodirector() != null) {
            codirector = docenteRepository.findByCorreo(request.getCodirector().getCorreo())
                    .orElseGet(() -> new Docente(
                            request.getCodirector().getNombre(),
                            request.getCodirector().getCorreo(),
                            request.getCodirector().getDepartamento()
                    ));

            Long trabajosCodirector = trabajoRepository.countTrabajosByProfesor(codirector.getCorreo());
            if (trabajosCodirector >= 3) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "El codirector " + codirector.getNombre() + " ya tiene 3 trabajos de grado."
                );
            }

            trabajo.setCodirector(codirector);
        }

        trabajo.setAnteproyecto(anteproyecto);
        anteproyecto.setTrabajoDeGrado(trabajo);

        Anteproyecto saved = anteproyectoRepository.save(anteproyecto);

        List<String> correos = new ArrayList<>();
        correos.addAll(saved.getTrabajoDeGrado().getEstudiantes()
                .stream()
                .map(Estudiante::getCorreo)
                .toList());
        correos.add(saved.getTrabajoDeGrado().getDirector().getCorreo());
        if (saved.getTrabajoDeGrado().getCodirector() != null) {
            correos.add(saved.getTrabajoDeGrado().getCodirector().getCorreo());
        }

        List<String> departamentos = new ArrayList<>();
        departamentos.add(saved.getTrabajoDeGrado().getDirector().getDepartamento());
        if (saved.getTrabajoDeGrado().getCodirector() != null) {
            departamentos.add(saved.getTrabajoDeGrado().getCodirector().getDepartamento());
        }

        AnteproyectoMessage message = new AnteproyectoMessage(
                saved.getTitulo(),
                saved.getDescripcion(),
                saved.getFechaCreacion(),
                saved.getTrabajoDeGrado().getEstudiantes()
                        .stream()
                        .map(Estudiante::getNombre)
                        .toList(),
                saved.getTrabajoDeGrado().getDirector().getNombre(),
                saved.getTrabajoDeGrado().getDirector().getDepartamento(),
                saved.getTrabajoDeGrado().getCodirector() != null
                        ? saved.getTrabajoDeGrado().getCodirector().getNombre()
                        : null,
                correos,
                departamentos
        );

        rabbitMQSender.sendAnteproyecto(message);

        return saved;
    }
}
