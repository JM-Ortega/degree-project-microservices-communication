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
 * Servicio encargado de gestionar la creación y validación de anteproyectos de grado.
 *
 * <p>Responsabilidades principales:
 * <ul>
 *   <li>Crear y persistir un {@link Anteproyecto} junto con su {@link TrabajoDeGrado} asociado.</li>
 *   <li>Evitar duplicados de {@link Estudiante} y {@link Docente} basándose en el correo electrónico.</li>
 *   <li>Validar que un estudiante no tenga más de un trabajo de grado.</li>
 *   <li>Validar que un docente no tenga más de 3 trabajos de grado asociados.</li>
 *   <li>Preparar y enviar un mensaje a RabbitMQ con la información del anteproyecto para notificaciones.</li>
 * </ul>
 * </p>
 *
 * <p>Este servicio se utiliza principalmente por {@link co.edu.unicauca.degreeprojectmicroservicescommunication.controller.SubmissionController}.</p>
 */
@Service
public class SubmissionService {
    @Autowired
    private AnteproyectoRepository anteproyectoRepository;

    @Autowired
    private EstudianteRepository estudianteRepository;

    @Autowired
    private DocenteRepository docenteRepository;

    @Autowired
    private TrabajoDeGradoRepository trabajoRepository;

    @Autowired
    private RabbitMQSender rabbitMQSender;

    /**
     * Crea un anteproyecto de grado a partir de la información recibida en {@link AnteproyectoRequest}.
     *
     * <p>Flujo de la operación:
     * <ol>
     *   <li>Se construye el {@link Anteproyecto} y {@link TrabajoDeGrado} a partir de la petición.</li>
     *   <li>Se buscan estudiantes y docentes existentes por correo para evitar duplicados; si no existen, se crean nuevos registros.</li>
     *   <li>Se valida que ningún estudiante tenga ya un trabajo de grado.</li>
     *   <li>Se valida que los docentes (director y codirector) no tengan más de 3 trabajos asociados.</li>
     *   <li>Se persiste el anteproyecto y el trabajo de grado en la base de datos.</li>
     *   <li>Se construye un {@link AnteproyectoMessage} con información relevante (nombres, correos y departamentos) y se envía a RabbitMQ.</li>
     * </ol>
     * </p>
     *
     * @param request DTO con la información necesaria para crear el anteproyecto
     * @return {@link Anteproyecto} persistido en la base de datos
     * @throws ResponseStatusException si:
     * <ul>
     *   <li>Algún estudiante ya tiene un trabajo de grado.</li>
     *   <li>Algún docente tiene 3 o más trabajos de grado asociados.</li>
     * </ul>
     */
    public Anteproyecto crearAnteproyecto(AnteproyectoRequest request) {
        // 1. Construcción del anteproyecto
        Anteproyecto anteproyecto = new Anteproyecto();
        anteproyecto.setTitulo(request.getTitulo());
        anteproyecto.setDescripcion(request.getDescripcion());
        anteproyecto.setFechaCreacion(new Date());

        TrabajoDeGrado trabajo = new TrabajoDeGrado();

        // 2. Mapear estudiantes
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

        // 3. Director obligatorio
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

        // 4. Codirector opcional
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

        // Asociaciones bidireccionales
        trabajo.setAnteproyecto(anteproyecto);
        anteproyecto.setTrabajoDeGrado(trabajo);

        // Persistir anteproyecto
        Anteproyecto saved = anteproyectoRepository.save(anteproyecto);

        // Crear el vector de correos (destinatarios)
        List<String> correos = new ArrayList<>();
        correos.addAll(saved.getTrabajoDeGrado().getEstudiantes()
                .stream()
                .map(Estudiante::getCorreo)
                .toList());
        correos.add(saved.getTrabajoDeGrado().getDirector().getCorreo());
        if (saved.getTrabajoDeGrado().getCodirector() != null) {
            correos.add(saved.getTrabajoDeGrado().getCodirector().getCorreo());
        }

        // Crear el vector de departamentos
        List<String> departamentos = new ArrayList<>();
        departamentos.add(saved.getTrabajoDeGrado().getDirector().getDepartamento());
        if (saved.getTrabajoDeGrado().getCodirector() != null) {
            departamentos.add(saved.getTrabajoDeGrado().getCodirector().getDepartamento());
        }

        // 5. Setear el mensaje a enviar
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

        // 6. Enviar el mensaje a la cola
        rabbitMQSender.sendAnteproyecto(message);

        // 7. Retorna el anteproyecto
        return saved;
    }
}
