package co.edu.unicauca.degreeprojectmicroservicescommunication.service;

import co.edu.unicauca.degreeprojectmicroservicescommunication.infra.communication.EmailMessage;
import jakarta.transaction.Transactional;

/**
 * Servicio que define las operaciones relacionadas con el envío de notificaciones.
 */
public interface INotificationService {
    /**
     * Envía un correo electrónico con el contenido especificado.
     * @param message objeto {@link EmailMessage} que contiene la información del correo (de, para, asunto y cuerpo del mensaje)
     */
    @Transactional
    void sendEmail(EmailMessage message);
}
