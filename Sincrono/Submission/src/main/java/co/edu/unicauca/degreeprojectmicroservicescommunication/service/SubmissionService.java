package co.edu.unicauca.degreeprojectmicroservicescommunication.service;

import co.edu.unicauca.degreeprojectmicroservicescommunication.entity.Anteproyecto;
import co.edu.unicauca.degreeprojectmicroservicescommunication.entity.Docente;
import co.edu.unicauca.degreeprojectmicroservicescommunication.entity.Estudiante;
import co.edu.unicauca.degreeprojectmicroservicescommunication.entity.TrabajoDeGrado;
import co.edu.unicauca.degreeprojectmicroservicescommunication.infra.dto.AnteproyectoRequest;
import co.edu.unicauca.degreeprojectmicroservicescommunication.repository.AnteproyectoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio responsable de gestionar la creación y envío de información de anteproyectos
 * dentro del microservicio Submission (versión SINCRÓNICA).
 *
 * <p>Este servicio se encarga de:
 * <ul>
 *   <li>Persistir la información del anteproyecto y sus entidades relacionadas (trabajo de grado, estudiantes, docentes).</li>
 *   <li>Construir el mensaje a enviar al microservicio Notification.</li>
 *   <li>Realizar una comunicación HTTP síncrona (POST) hacia el endpoint de notificaciones.</li>
 * </ul>
 *
 * <p>En esta versión, NO se utiliza RabbitMQ. La comunicación se hace directamente mediante {@link RestClient},
 * configurado con la propiedad <b>notification.base-url</b> del archivo <i>application.yml</i>.
 */
@Service
public class SubmissionService {

    /**
     * Repositorio encargado de las operaciones CRUD sobre la entidad {@link Anteproyecto}.
     */
    private final AnteproyectoRepository anteproyectoRepository;

    /**
     * Cliente HTTP usado para comunicarse con el microservicio Notification.
     */
    private final RestClient notificationClient;

    /**
     * Constructor del servicio, con inyección de dependencias.
     *
     * @param anteproyectoRepository repositorio de anteproyectos
     * @param notificationBaseUrl    URL base del microservicio Notification (definida en <i>application.yml</i>)
     */
    public SubmissionService(AnteproyectoRepository anteproyectoRepository,
                             @Value("${notification.base-url}") String notificationBaseUrl) {
        this.anteproyectoRepository = anteproyectoRepository;
        this.notificationClient = RestClient.create(notificationBaseUrl);
    }

    /**
     * Crea un nuevo anteproyecto en la base de datos y notifica al microservicio Notification.
     *
     * <p>Este método realiza las siguientes operaciones:
     * <ol>
     *   <li>Construye las entidades {@link Anteproyecto}, {@link TrabajoDeGrado}, {@link Estudiante} y {@link Docente}
     *       a partir de los datos recibidos.</li>
     *   <li>Guarda el anteproyecto en la base de datos mediante JPA.</li>
     *   <li>Construye un payload simple con la información relevante (título, descripción, correos, departamento).</li>
     *   <li>Envía dicho payload al endpoint <b>/notify/anteproyecto</b> del microservicio Notification usando HTTP POST.</li>
     * </ol>
     *
     * @param request Objeto DTO con los datos del anteproyecto enviados desde el cliente.
     * @return La entidad {@link Anteproyecto} persistida en la base de datos.
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
                .map(e -> new Estudiante(e.getNombre(), e.getCorreo(), e.getCodigo()))
                .collect(Collectors.toList());
        trabajo.setEstudiantes(estudiantes);

        // 3. Director obligatorio
        Docente director = new Docente(
                request.getDirector().getNombre(),
                request.getDirector().getCorreo(),
                request.getDirector().getDepartamento()
        );
        trabajo.setDirector(director);

        // 4. Codirector opcional
        if (request.getCodirector() != null) {
            Docente codirector = new Docente(
                    request.getCodirector().getNombre(),
                    request.getCodirector().getCorreo(),
                    request.getCodirector().getDepartamento()
            );
            trabajo.setCodirector(codirector);
        }

        // Asociaciones bidireccionales
        trabajo.setAnteproyecto(anteproyecto);
        anteproyecto.setTrabajoDeGrado(trabajo);

        // Persistir anteproyecto
        Anteproyecto saved = anteproyectoRepository.save(anteproyecto);

        // 5. Construir payload simplificado para notificación
        var tg = saved.getTrabajoDeGrado();
        String estudiante1 = tg.getEstudiantes().isEmpty() ? null : tg.getEstudiantes().get(0).getCorreo();
        String estudiante2 = tg.getEstudiantes().size() > 1 ? tg.getEstudiantes().get(1).getCorreo() : null;
        String directorEmail = tg.getDirector() != null ? tg.getDirector().getCorreo() : null;
        String codirectorEmail = tg.getCodirector() != null ? tg.getCodirector().getCorreo() : null;

        String departamento = request.getDirector() != null ? request.getDirector().getDepartamento() : null;
        if (request.getDepartamento() != null && !request.getDepartamento().isBlank()) {
            departamento = request.getDepartamento();
        }

        var payload = new NotifyAnteproyectoPayload(
                saved.getTitulo(),
                saved.getDescripcion(),
                estudiante1,
                estudiante2,
                directorEmail,
                codirectorEmail,
                departamento
        );

        // 6. Envío síncrono HTTP al microservicio Notification
        ResponseEntity<Void> resp = notificationClient.post()
                .uri("/notify/anteproyecto")
                .body(payload)
                .retrieve()
                .toBodilessEntity();

        // (Opcional) registrar resultado
        if (!resp.getStatusCode().is2xxSuccessful()) {
            System.err.println("Error al enviar notificación: " + resp.getStatusCode());
        }

        return saved;
    }

    /**
     * Record local que define el formato JSON que se envía al microservicio Notification.
     *
     * <p>Equivale a los campos esperados en su endpoint <b>/notify/anteproyecto</b>.</p>
     *
     * @param titulo       título del anteproyecto
     * @param descripcion  descripción breve del anteproyecto
     * @param estudiante1  correo del primer estudiante
     * @param estudiante2  correo del segundo estudiante (opcional)
     * @param director     correo del docente director
     * @param codirector   correo del codirector (opcional)
     * @param departamento nombre del departamento asociado
     */
    private record NotifyAnteproyectoPayload(
            String titulo,
            String descripcion,
            String estudiante1,
            String estudiante2,
            String director,
            String codirector,
            String departamento
    ) {
    }
}
