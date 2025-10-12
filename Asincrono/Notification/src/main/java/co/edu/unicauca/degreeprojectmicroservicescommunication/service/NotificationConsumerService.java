package co.edu.unicauca.degreeprojectmicroservicescommunication.service;

import co.edu.unicauca.degreeprojectmicroservicescommunication.Entities.Departamento;
import co.edu.unicauca.degreeprojectmicroservicescommunication.Repository.JefeDeptoRepository;
import co.edu.unicauca.degreeprojectmicroservicescommunication.infra.communication.EmailMessage;
import co.edu.unicauca.degreeprojectmicroservicescommunication.infra.dto.AnteproyectoRequest;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
        String de = "Servicio de notificaciones";
        List<String> para = new ArrayList<String>();
        String asunto = "Nuevo anteproyecto de grado";
        String body = "Apreciado usuario. \n" +
                "Esta notificación es para informarle que se registró un nuevo anteproyecto de grado. \n" +
                "Titulo del anteproyecto: " + anteproyectoRequest.getTitulo() + "\n" +
                "Descripción: " + anteproyectoRequest.getDescripcion() + "\n";

        para.add(anteproyectoRequest.getEstudiante1());

        if(anteproyectoRequest.getEstudiante2()!=null && !anteproyectoRequest.getEstudiante2().isEmpty()){
            para.add(anteproyectoRequest.getEstudiante2());
            body = body + "Autor(es): " + anteproyectoRequest.getEstudiante1() + " y " + anteproyectoRequest.getEstudiante2() + "\n";
        }else{
            body = body + "Autor(es): " + anteproyectoRequest.getEstudiante1() + "\n";
        }

        String depto = anteproyectoRequest.getDepartamento().toLowerCase();
        if(depto.contains("sistemas")){
            Departamento EnumDepto = Departamento.SISTEMAS;
            jefeDeptoRepository.findByDepto(EnumDepto).ifPresent(jefeDepto -> para.add(jefeDepto.getEmail()));
        }else if(depto.contains("electronica")){
            Departamento EnumDepto = Departamento.ELECTRONICA_INSTRUMENTACION_Y_CONTROL;
            jefeDeptoRepository.findByDepto(EnumDepto).ifPresent(jefeDepto -> para.add(jefeDepto.getEmail()));
        }else if(depto.contains("telematica")){
            Departamento EnumDepto = Departamento.TELEMATICA;
            jefeDeptoRepository.findByDepto(EnumDepto).ifPresent(jefeDepto -> para.add(jefeDepto.getEmail()));
        }else if(depto.contains("telecomunicaciones")){
            Departamento EnumDepto = Departamento.TELECOMUNICACIONES;
            jefeDeptoRepository.findByDepto(EnumDepto).ifPresent(jefeDepto -> para.add(jefeDepto.getEmail()));
        }else {
            System.out.println("No se reconoció el departamento");
        }

        if(anteproyectoRequest.getDirector()!=null && !anteproyectoRequest.getDirector().isEmpty() &&
           anteproyectoRequest.getCodirector()!=null && !anteproyectoRequest.getCodirector().isEmpty()){
            para.add(anteproyectoRequest.getDirector());
            para.add(anteproyectoRequest.getCodirector());
        }

        EmailMessage message = new EmailMessage(asunto, body, de , para);
        notificationService.sendEmail(message);
    }
}