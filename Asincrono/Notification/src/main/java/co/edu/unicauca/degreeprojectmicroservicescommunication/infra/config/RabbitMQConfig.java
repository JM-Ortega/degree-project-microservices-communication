package co.edu.unicauca.degreeprojectmicroservicescommunication.infra.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de RabbitMQ para el servicio de comunicación entre microservicios.
 * <p>
 * Esta clase define los componentes necesarios para la comunicación con RabbitMQ, incluyendo
 * la cola, el exchange, la routing key y el convertidor de mensajes JSON.
 * Los valores de configuración se obtienen desde las propiedades definidas en
 * el archivo <b>application.yml</b>.
 * </p>
 */
@Configuration
public class RabbitMQConfig {
    /**
     * Nombre del exchange definido en la configuración.
     */
    @Value("${app.rabbit.exchange}")
    private String exchange;

    /**
     * Nombre de la cola utilizada por la aplicación.
     */
    @Value("${app.rabbit.queue}")
    private String queue;

    /**
     * Routing key que asocia el exchange con la cola.
     */
    @Value("${app.rabbit.routing-key}")
    private String routingKey;

    /**
     * Declara la cola que recibirá los mensajes de RabbitMQ.
     * @return una instancia de {@link Queue} configurada como duradera.
     */
    @Bean
    public Queue queue() {
        return new Queue(queue, true);
    }

    /**
     * Declara el exchange de tipo {@link TopicExchange} que distribuirá los mensajes.
     * @return una instancia de {@link TopicExchange}.
     */
    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(exchange);
    }

    /**
     * Crea la asociación entre la cola y el exchange utilizando la routing key especificada.
     * @param queue la cola que recibirá los mensajes.
     * @param exchange el exchange responsable de enrutar los mensajes.
     * @return una instancia de {@link Binding} que une la cola y el exchange.
     */
    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(routingKey);
    }

    /**
     * Configura un convertidor de mensajes que permite la serialización y
     * deserialización de objetos Java a formato JSON.
     * @return una instancia de {@link Jackson2JsonMessageConverter}.
     */
    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
