package co.edu.unicauca.degreeprojectmicroservicescommunication.service;

import co.edu.unicauca.degreeprojectmicroservicescommunication.infra.communication.EmailMessage;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

/**
 * Implementación del servicio de notificaciones encargado de enviar correos electrónicos simulados.
 */
@Service
public class NotificationService implements INotificationService {
    /**
     * Logger utilizado para registrar la simulación del envío de correos.
     */
    private static final Logger logger = Logger.getLogger(NotificationService.class.getName());

    /**
     * Simula el envío de un correo electrónico mostrando los detalles del mensaje en los logs.
     * @param message objeto {@link EmailMessage} que contiene la información del correo (de, para, asunto y cuerpo del mensaje)
     */
    @Override
    public void sendEmail(EmailMessage message) {
        logger.info("----- SIMULACION DE ENVIO DE EMAIL -----\n" +
                "De: " + message.de + "\n" +
                "Para: " + String.join(", ", message.para) + "\n" +
                "Asunto: " + message.asunto + "\n" +
                "Body:\n" + message.body + "\n" +
                "----- END SIMULATED EMAIL -----\n");
    }
}
