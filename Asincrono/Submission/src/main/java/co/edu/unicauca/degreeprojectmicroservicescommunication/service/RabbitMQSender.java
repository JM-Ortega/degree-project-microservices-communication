package co.edu.unicauca.degreeprojectmicroservicescommunication.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Servicio responsable de enviar mensajes a RabbitMQ.
 *
 * <p>Esta clase utiliza un {@link RabbitTemplate} configurado para publicar mensajes
 * en un exchange específico con una routing key definida en el archivo de propiedades
 * de la aplicación.</p>
 *
 * <p>Actualmente se utiliza para enviar los datos de los anteproyectos creados,
 * en forma de un DTO.</p>
 *
 * @author Laura Molano
 */
@Service
public class RabbitMQSender {
    /** Plantilla de RabbitMQ utilizada para enviar mensajes. */
    private final RabbitTemplate rabbitTemplate;

    /** Nombre del exchange configurado en las propiedades de la aplicación. */
    @Value("${app.rabbit.exchange}")
    private String exchange;

    /** Routing key configurada en las propiedades de la aplicación. */
    @Value("${app.rabbit.routing-key}")
    private String routingKey;

    /**
     * Constructor que inyecta el {@link RabbitTemplate}.
     *
     * @param rabbitTemplate plantilla usada para la comunicación con RabbitMQ.
     */
    public RabbitMQSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Envía un mensaje al exchange configurado usando la routing key.
     *
     * <p>El mensaje se convierte automáticamente a JSON gracias al
     * {@link org.springframework.amqp.support.converter.Jackson2JsonMessageConverter}
     * definido en la configuración de RabbitMQ.</p>
     *
     * @param message el objeto que se desea enviar a RabbitMQ.
     */
    public void sendAnteproyecto(Object message) {
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
        System.out.println(":) :) :) Mensaje publicado en RabbitMQ: " + message + " :) :) :)");
    }
}
