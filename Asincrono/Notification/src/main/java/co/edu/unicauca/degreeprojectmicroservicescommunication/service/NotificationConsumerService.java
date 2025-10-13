package co.edu.unicauca.degreeprojectmicroservicescommunication.service;

import co.edu.unicauca.degreeprojectmicroservicescommunication.Entities.Departamento;
import co.edu.unicauca.degreeprojectmicroservicescommunication.Repository.JefeDeptoRepository;
import co.edu.unicauca.degreeprojectmicroservicescommunication.infra.communication.EmailMessage;
import co.edu.unicauca.degreeprojectmicroservicescommunication.infra.dto.AnteproyectoRequest;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Servicio encargado de consumir mensajes desde RabbitMQ relacionados con anteproyectos de grado.
 * <p>
 * Este componente escucha la cola configurada en la propiedad <b>app.rabbit.queue</b> y procesa
 * los mensajes de tipo {@link AnteproyectoRequest}. Al recibir un mensaje, construye y envía
 * una notificación por correo electrónico simulada a los autores, director(es) y jefe del departamento correspondiente.
 * </p>
 */
@Service
public class NotificationConsumerService {

    /**
     * Servicio encargado del envío (simulado) de correos electrónicos.
     */
    @Autowired
    private INotificationService notificationService;

    /**
     * Repositorio que permite acceder a la información de los jefes de departamento.
     */
    @Autowired
    private JefeDeptoRepository jefeDeptoRepository;

    /**
     * Método que escucha los mensajes provenientes de RabbitMQ.
     * <p>
     * Cuando se recibe un mensaje con los datos de un anteproyecto, se construye un correo electrónico
     * con la información del título, descripción, autores y departamento correspondiente.
     * Además, se agregan como destinatarios los correos de los autores, el director, el codirector
     * (si existe) y el jefe del departamento detectado.
     * </p>
     * @param anteproyectoRequest objeto {@link AnteproyectoRequest} recibido desde la cola de RabbitMQ, que contiene los datos del anteproyecto de grado.
     */
    @RabbitListener(queues = "${app.rabbit.queue}")
    public void receiveMessage(AnteproyectoRequest anteproyectoRequest) {
        if (anteproyectoRequest == null) {
            System.out.println("El mensaje recibido es nulo");
            return;
        }

        String de = "Servicio de notificaciones";
        List<String> para = new ArrayList<>();
        String asunto = "Nuevo anteproyecto de grado";

        String body = "Apreciado usuario,\n" +
                "Se registró un nuevo anteproyecto de grado.\n" +
                "Título: " + anteproyectoRequest.getTitulo() + "\n" +
                "Descripción: " + anteproyectoRequest.getDescripcion() + "\n";

        if (anteproyectoRequest.getEstudiantes() != null && !anteproyectoRequest.getEstudiantes().isEmpty()) {
            body += "Autor(es): " + String.join(", ", anteproyectoRequest.getEstudiantes()) + "\n";
        }

        if (anteproyectoRequest.getDirector() != null && !anteproyectoRequest.getDirector().isEmpty()) {
            body += "Director: " + anteproyectoRequest.getDirector() + "\n";
        }

        if (anteproyectoRequest.getCodirector() != null && !anteproyectoRequest.getCodirector().isEmpty()) {
            body += "Codirector: " + anteproyectoRequest.getCodirector() + "\n";
        }

        if (anteproyectoRequest.getDepartamentos() != null && !anteproyectoRequest.getDepartamentos().isEmpty()) {
            Set<Departamento> departamentosReconocidos = new HashSet<>();

            for (String depto : anteproyectoRequest.getDepartamentos()) {
                String d = depto.toLowerCase();
                Departamento enumDepto = null;

                if (d.contains("sistemas")) {
                    enumDepto = Departamento.SISTEMAS;
                } else if (d.contains("electrónica") || d.contains("electronica")) {
                    enumDepto = Departamento.ELECTRONICA_INSTRUMENTACION_Y_CONTROL;
                } else if (d.contains("telemática") || d.contains("telematica")) {
                    enumDepto = Departamento.TELEMATICA;
                } else if (d.contains("telecomunicaciones")) {
                    enumDepto = Departamento.TELECOMUNICACIONES;
                }

                if (enumDepto != null) {
                    departamentosReconocidos.add(enumDepto);
                } else {
                    System.out.println("No se reconoció el departamento: " + depto);
                }
            }

            for (Departamento depto : departamentosReconocidos) {
                jefeDeptoRepository.findByDepto(depto)
                        .ifPresent(jefe -> para.add(jefe.getEmail()));
            }
        }


        if (anteproyectoRequest.getCorreos() != null && !anteproyectoRequest.getCorreos().isEmpty()) {
            para.addAll(anteproyectoRequest.getCorreos());
        }

        EmailMessage message = new EmailMessage(asunto, body, de, para);
        notificationService.sendEmail(message);
    }
}