package co.edu.unicauca.degreeprojectmicroservicescommunication.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQSender {
    private final RabbitTemplate rabbitTemplate;

    @Value("${app.rabbit.exchange}")
    private String exchange;

    @Value("${app.rabbit.routing-key}")
    private String routingKey;

    public RabbitMQSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendAnteproyecto(Object message) {
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
        System.out.println(":) :) :) Mensaje publicado en RabbitMQ: " + message+" :) :) :)");
    }
}
