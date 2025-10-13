package co.edu.unicauca.degreeprojectmicroservicescommunication.service;

import co.edu.unicauca.degreeprojectmicroservicescommunication.dto.AnteproyectoMessageDTO;
import co.edu.unicauca.degreeprojectmicroservicescommunication.dto.AnteproyectoRequest;
import co.edu.unicauca.degreeprojectmicroservicescommunication.entity.Anteproyecto;
import co.edu.unicauca.degreeprojectmicroservicescommunication.entity.Docente;
import co.edu.unicauca.degreeprojectmicroservicescommunication.entity.Estudiante;
import co.edu.unicauca.degreeprojectmicroservicescommunication.entity.TrabajoDeGrado;
import co.edu.unicauca.degreeprojectmicroservicescommunication.repository.AnteproyectoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubmissionService {
    @Autowired
    private AnteproyectoRepository anteproyectoRepository;

    @Autowired
    private RabbitMQSender rabbitMQSender;

    public Anteproyecto crearAnteproyecto(AnteproyectoRequest request) {
        Anteproyecto anteproyecto = new Anteproyecto();
        anteproyecto.setTitulo(request.getTitulo());
        anteproyecto.setDescripcion(request.getDescripcion());
        anteproyecto.setFechaCreacion(new Date());

        TrabajoDeGrado trabajo = new TrabajoDeGrado();

        // Mapear estudiantes
        List<Estudiante> estudiantes = request.getEstudiantes()
                .stream()
                .map(e -> new Estudiante(e.getNombre(), e.getCorreo(), e.getCodigo()))
                .collect(Collectors.toList());

        trabajo.setEstudiantes(estudiantes);

        // Director
        Docente director = new Docente(
                request.getDirector().getNombre(),
                request.getDirector().getCorreo(),
                request.getDirector().getDepartamento()
        );
        trabajo.setDirector(director);

        // Codirector opcional
        if (request.getCodirector() != null) {
            Docente codirector = new Docente(
                    request.getCodirector().getNombre(),
                    request.getCodirector().getCorreo(),
                    request.getCodirector().getDepartamento()
            );
            trabajo.setCodirector(codirector);
        }

        trabajo.setAnteproyecto(anteproyecto);
        anteproyecto.setTrabajoDeGrado(trabajo);

        Anteproyecto saved = anteproyectoRepository.save(anteproyecto);

        // Crear DTO para enviar a RabbitMQ
        AnteproyectoMessageDTO message = new AnteproyectoMessageDTO(
                saved.getTitulo(),
                saved.getDescripcion(),
                saved.getFechaCreacion(),
                saved.getTrabajoDeGrado().getEstudiantes()
                        .stream()
                        .map(Estudiante::getNombre)
                        .toList(),
                saved.getTrabajoDeGrado().getDirector().getNombre(),
                saved.getTrabajoDeGrado().getCodirector() != null
                        ? saved.getTrabajoDeGrado().getCodirector().getNombre()
                        : null
        );

        rabbitMQSender.sendAnteproyecto(message); // envía un DTO, no una entidad

        return saved;
    }
}
