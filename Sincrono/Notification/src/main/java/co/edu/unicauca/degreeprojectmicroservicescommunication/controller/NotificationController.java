package co.edu.unicauca.degreeprojectmicroservicescommunication.controller;

import co.edu.unicauca.degreeprojectmicroservicescommunication.infra.communication.EmailMessage;
import co.edu.unicauca.degreeprojectmicroservicescommunication.infra.communication.NotificationComposer;
import co.edu.unicauca.degreeprojectmicroservicescommunication.infra.dto.AnteproyectoRequest;
import co.edu.unicauca.degreeprojectmicroservicescommunication.service.INotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notify")
@RequiredArgsConstructor
public class NotificationController {

  private static final Logger log = LoggerFactory.getLogger(NotificationController.class);

  private final NotificationComposer composer;
  private final INotificationService notificationService;

  /**
   * Endpoint síncrono del escenario 2:
   * Submission envía el anteproyecto y este servicio "envía" el correo (simulado con logs).
   */
  @PostMapping("/anteproyecto")
  public ResponseEntity<Void> notifyAnteproyecto(@Valid @RequestBody AnteproyectoRequest req) {
    EmailMessage message = composer.composeAnteproyecto(req);
    // Simula envío (usa tu NotificationService existente)
    notificationService.sendEmail(message);
    // Log corto adicional (útil para ver el 200)
    log.info("Notificación generada para anteproyecto: {}", req.getTitulo());
    return ResponseEntity.ok().build();
  }
}
