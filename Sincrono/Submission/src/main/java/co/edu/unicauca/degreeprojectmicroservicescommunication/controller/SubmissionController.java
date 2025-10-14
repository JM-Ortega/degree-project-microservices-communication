package co.edu.unicauca.degreeprojectmicroservicescommunication.controller;

import co.edu.unicauca.degreeprojectmicroservicescommunication.entity.Anteproyecto;
import co.edu.unicauca.degreeprojectmicroservicescommunication.infra.dto.AnteproyectoRequest;
import co.edu.unicauca.degreeprojectmicroservicescommunication.service.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

/**
 * Controlador del microservicio Submission.
 *
 * <p>Gestiona las solicitudes relacionadas con la creación y envío de anteproyectos.
 * Este controlador recibe los datos del anteproyecto, los valida y delega la
 * lógica de negocio al {@link SubmissionService}, que se encarga de persistir
 * la información y comunicar al microservicio Notification de forma síncrona.</p>
 *
 * <p>Ruta base: <b>/api/submission</b></p>
 */
@RestController
@RequestMapping("/api/submission")
public class SubmissionController {

    private static final Logger logger = Logger.getLogger(SubmissionController.class.getName());

    /** Servicio encargado de la lógica de creación de anteproyectos. */
    @Autowired
    private SubmissionService submissionService;

    /**
     * Endpoint para registrar un nuevo anteproyecto y notificar al servicio Notification.
     *
     * <p>Este método representa la comunicación <b>síncrona</b> del escenario 2 del taller.
     * Al recibir un {@link AnteproyectoRequest}, se valida y se guarda el anteproyecto,
     * luego se realiza una llamada HTTP al microservicio Notification.</p>
     *
     * @param request Objeto con la información del anteproyecto.
     * @return {@link ResponseEntity} con el anteproyecto creado o un mensaje de error.
     */
    @PostMapping("/anteproyecto")
    public ResponseEntity<?> crearAnteproyecto(@RequestBody AnteproyectoRequest request) {
        try {
            logger.info("Solicitud recibida para crear anteproyecto: " + request.getTitulo());

            Anteproyecto saved = submissionService.crearAnteproyecto(request);

            logger.info("Anteproyecto registrado exitosamente: " + saved.getTitulo());
            return ResponseEntity.ok("Anteproyecto registrado y notificado correctamente");
        } catch (Exception e) {
            logger.severe("Error al registrar el anteproyecto: " + e.getMessage());
            return ResponseEntity.internalServerError()
                    .body("Error al registrar el anteproyecto: " + e.getMessage());
        }
    }
}
