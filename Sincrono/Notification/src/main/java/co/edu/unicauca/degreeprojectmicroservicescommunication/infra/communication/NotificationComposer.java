package co.edu.unicauca.degreeprojectmicroservicescommunication.infra.communication;

import co.edu.unicauca.degreeprojectmicroservicescommunication.Entities.Departamento;
import co.edu.unicauca.degreeprojectmicroservicescommunication.Repository.JefeDeptoRepository;
import co.edu.unicauca.degreeprojectmicroservicescommunication.infra.dto.AnteproyectoRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class NotificationComposer {

    private final JefeDeptoRepository jefeDeptoRepository;

    /**
     * Construye el correo a partir del request del anteproyecto.
     * Incluye: jefe de departamento, estudiante(s), director y codirector (si existen).
     */
    public EmailMessage composeAnteproyecto(AnteproyectoRequest req) {
        String de = "notificaciones@uni.edu";
        String asunto = "Nuevo anteproyecto de grado";
        StringBuilder body = new StringBuilder()
                .append("Apreciado usuario,\n")
                .append("Se registró un nuevo anteproyecto de grado.\n")
                .append("Título: ").append(req.getTitulo()).append("\n")
                .append("Descripción: ").append(req.getDescripcion()).append("\n");

        List<String> para = new ArrayList<>();

        // Estudiantes
        if (req.getEstudiante1() != null && !req.getEstudiante1().isBlank()) {
            para.add(req.getEstudiante1());
        }
        if (req.getEstudiante2() != null && !req.getEstudiante2().isBlank()) {
            para.add(req.getEstudiante2());
            body.append("Autor(es): ").append(req.getEstudiante1())
                    .append(" y ").append(req.getEstudiante2()).append("\n");
        } else if (req.getEstudiante1() != null) {
            body.append("Autor(es): ").append(req.getEstudiante1()).append("\n");
        }

        // Director y Codirector (opcionales)
        if (req.getDirector() != null && !req.getDirector().isBlank()) {
            para.add(req.getDirector());
        }
        if (req.getCodirector() != null && !req.getCodirector().isBlank()) {
            para.add(req.getCodirector());
        }

        // Jefe de departamento (a partir del String del request)
        Departamento enumDepto = req.getDepartamento();
        if (enumDepto != null) {
            jefeDeptoRepository.findByDepto(enumDepto).ifPresent(jefe -> para.add(jefe.getEmail()));
        }

        // IMPORTANTE: respetamos tu constructor: (asunto, body, de, para)
        return new EmailMessage(asunto, body.toString(), de, para);
    }

    /**
     * Intenta mapear el string del request a tu enum Departamento.
     * Retorna null si no reconoce el texto.
     */
    private Departamento inferDepartamento(String deptoStr) {
        if (deptoStr == null) return null;
        String depto = deptoStr.toLowerCase();
        if (depto.contains("sistemas")) return Departamento.SISTEMAS;
        if (depto.contains("electronica")) return Departamento.ELECTRONICA_INSTRUMENTACION_Y_CONTROL;
        if (depto.contains("telematica")) return Departamento.TELEMATICA;
        if (depto.contains("telecomunicaciones")) return Departamento.TELECOMUNICACIONES;
        return null;
    }
}
