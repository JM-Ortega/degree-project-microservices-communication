package co.edu.unicauca.degreeprojectmicroservicescommunication.infra.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de RabbitMQ para la aplicación.
 *
 * <p>Esta clase define los beans necesarios para establecer la comunicación con RabbitMQ:
 * el exchange, la cola, el binding entre ambos, y la configuración del {@link RabbitTemplate}
 * con un conversor JSON.</p>
 *
 * <p>Los valores del exchange, la cola y la routing key se obtienen desde el archivo
 * de propiedades de la aplicación (application.yml).</p>
 */
@Configuration
public class RabbitMQConfig {
    /** Nombre del exchange definido en las propiedades de la aplicación. */
    @Value("${app.rabbit.exchange}")
    private String exchange;

    /** Nombre de la cola definida en las propiedades de la aplicación. */
    @Value("${app.rabbit.queue}")
    private String queue;

    /** Routing key utilizada para enlazar el exchange con la cola. */
    @Value("${app.rabbit.routing-key}")
    private String routingKey;

    /**
     * Crea un exchange de tipo {@link TopicExchange}.
     *
     * @return una instancia de {@link TopicExchange} configurada con el nombre especificado.
     */
    @Bean
    public TopicExchange thesisExchange() {
        return new TopicExchange(exchange);
    }

    /**
     * Crea una cola durable que persiste si RabbitMQ se reinicia.
     *
     * @return una instancia de {@link Queue} con el nombre especificado.
     */
    @Bean
    public Queue thesisQueue() {
        return new Queue(queue, true);
    }

    /**
     * Crea un binding que enlaza la cola con el exchange usando la routing key.
     *
     * @param thesisQueue la cola que será enlazada.
     * @param thesisExchange el exchange al cual se enlazará la cola.
     * @return una instancia de {@link Binding} que representa la relación entre la cola y el exchange.
     */
    @Bean
    public Binding thesisBinding(Queue thesisQueue, TopicExchange thesisExchange) {
        return BindingBuilder
                .bind(thesisQueue)
                .to(thesisExchange)
                .with(routingKey);
    }

    /**
     * Define un conversor de mensajes basado en JSON utilizando {@link Jackson2JsonMessageConverter}.
     *
     * @return una instancia de {@link Jackson2JsonMessageConverter}.
     */
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * Configura un {@link RabbitTemplate} para enviar y recibir mensajes,
     * utilizando el conversor JSON definido previamente.
     *
     * @param connectionFactory la fábrica de conexiones utilizada por el template.
     * @return una instancia configurada de {@link RabbitTemplate}.
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}
