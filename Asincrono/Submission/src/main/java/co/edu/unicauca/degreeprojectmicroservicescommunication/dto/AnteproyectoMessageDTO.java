package co.edu.unicauca.degreeprojectmicroservicescommunication.dto;

import java.util.Date;
import java.util.List;

public class AnteproyectoMessageDTO {
    private String titulo;
    private String descripcion;
    private Date fechaCreacion;
    private List<String> estudiantes;
    private String director;
    private String codirector;

    public AnteproyectoMessageDTO() {}

    public AnteproyectoMessageDTO(String titulo, String descripcion, Date fechaCreacion,
                                  List<String> estudiantes, String director, String codirector) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fechaCreacion = fechaCreacion;
        this.estudiantes = estudiantes;
        this.director = director;
        this.codirector = codirector;
    }

    public String getTitulo() { return titulo; }
    public String getDescripcion() { return descripcion; }
    public Date getFechaCreacion() { return fechaCreacion; }
    public List<String> getEstudiantes() { return estudiantes; }
    public String getDirector() { return director; }
    public String getCodirector() { return codirector; }

    public void setTitulo(String titulo) { this.titulo = titulo; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setFechaCreacion(Date fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public void setEstudiantes(List<String> estudiantes) { this.estudiantes = estudiantes; }
    public void setDirector(String director) { this.director = director; }
    public void setCodirector(String codirector) { this.codirector = codirector; }
}
