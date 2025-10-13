package co.edu.unicauca.degreeprojectmicroservicescommunication.controller;

import co.edu.unicauca.degreeprojectmicroservicescommunication.infra.dto.AnteproyectoRequest;
import co.edu.unicauca.degreeprojectmicroservicescommunication.entity.Anteproyecto;
import co.edu.unicauca.degreeprojectmicroservicescommunication.service.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST que gestiona las operaciones relacionadas con el envío de anteproyectos.
 *
 * <p>Expone los endpoints del servicio de envíos (submissions) bajo la ruta
 * <code>/api/submission</code>. Actualmente permite crear un nuevo anteproyecto.</p>
 *
 * <p>Este controlador delega la lógica de negocio al {@link SubmissionService}.</p>
 */
@RestController
@RequestMapping("/api/submission")
public class SubmissionController {
    /** Servicio encargado de manejar la lógica relacionada con los envíos (submissions). */
    @Autowired
    private SubmissionService submissionService;

    /**
     * Crea un nuevo anteproyecto a partir de los datos enviados en la solicitud.
     *
     * <p>Si la creación es exitosa, devuelve el anteproyecto guardado con un estado HTTP 200 (OK).
     * Si ocurre un error, devuelve una respuesta con estado HTTP 400 (Bad Request)
     * y un mensaje descriptivo en formato JSON.</p>
     *
     * @param request objeto que contiene los datos del anteproyecto a crear.
     * @return una {@link ResponseEntity} que contiene el anteproyecto creado o un mensaje de error.
     */
    @PostMapping("/anteproyecto")
    public ResponseEntity<?> createAnteproyecto(@RequestBody AnteproyectoRequest request) {
        try {
            Anteproyecto saved = submissionService.crearAnteproyecto(request);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}
