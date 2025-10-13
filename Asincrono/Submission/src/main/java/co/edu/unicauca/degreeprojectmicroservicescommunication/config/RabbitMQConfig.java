package co.edu.unicauca.degreeprojectmicroservicescommunication.config;

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

@Configuration
public class RabbitMQConfig {
    @Value("${app.rabbit.exchange}")
    private String exchange;

    @Value("${app.rabbit.queue}")
    private String queue;

    @Value("${app.rabbit.routing-key}")
    private String routingKey;

    // Crea el Exchange (tipo Topic)
    @Bean
    public TopicExchange thesisExchange() {
        return new TopicExchange(exchange);
    }

    // Crea la Queue
    @Bean
    public Queue thesisQueue() {
        return new Queue(queue, true); // true = durable (se mantiene si RabbitMQ reinicia)
    }

    // Enlazar Queue con Exchange usando el routing key
    @Bean
    public Binding thesisBinding(Queue thesisQueue, TopicExchange thesisExchange) {
        return BindingBuilder
                .bind(thesisQueue)
                .to(thesisExchange)
                .with(routingKey);
    }

    // Conversor JSON
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // RabbitTemplate configurado con el conversor
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}
