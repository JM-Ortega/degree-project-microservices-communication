package co.edu.unicauca.degreeprojectmicroservicescommunication.service;

import co.edu.unicauca.degreeprojectmicroservicescommunication.infra.dto.AnteproyectoMessage;
import co.edu.unicauca.degreeprojectmicroservicescommunication.infra.dto.AnteproyectoRequest;
import co.edu.unicauca.degreeprojectmicroservicescommunication.entity.Anteproyecto;
import co.edu.unicauca.degreeprojectmicroservicescommunication.entity.Docente;
import co.edu.unicauca.degreeprojectmicroservicescommunication.entity.Estudiante;
import co.edu.unicauca.degreeprojectmicroservicescommunication.entity.TrabajoDeGrado;
import co.edu.unicauca.degreeprojectmicroservicescommunication.repository.AnteproyectoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
     *   <li>Se mapean los estudiantes, el director y (opcionalmente) el codirector.</li>
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
                .map(e -> new Estudiante(e.getNombre(), e.getCorreo(), e.getCodigo()))
                .collect(Collectors.toList());

        trabajo.setEstudiantes(estudiantes);

        Docente director = new Docente(
                request.getDirector().getNombre(),
                request.getDirector().getCorreo(),
                request.getDirector().getDepartamento()
        );
        trabajo.setDirector(director);

        if (request.getCodirector() != null) {
            Docente codirector = new Docente(
                    request.getCodirector().getNombre(),
                    request.getCodirector().getCorreo(),
                    request.getCodirector().getDepartamento()
            );
            trabajo.setCodirector(codirector);
        }

        trabajo.setAnteproyecto(anteproyecto);
        anteproyecto.setTrabajoDeGrado(trabajo);

        Anteproyecto saved = anteproyectoRepository.save(anteproyecto);

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
                        : null
        );

        rabbitMQSender.sendAnteproyecto(message);

        return saved;
    }
}
