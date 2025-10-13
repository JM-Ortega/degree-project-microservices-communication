package co.edu.unicauca.degreeprojectmicroservicescommunication.infra.dto;

import java.util.Date;
import java.util.List;

public class AnteproyectoMessage {
    private String titulo;
    private String descripcion;
    private Date fechaCreacion;
    private List<String> estudiantes;
    private String director;
    private String codirector;
    private List<String> correos;
    private List<String> departamentos;

    public AnteproyectoMessage() {}

    public AnteproyectoMessage(String titulo, String descripcion, Date fechaCreacion,
                               List<String> estudiantes, String director, String departamento,
                               String codirector, List<String> correos, List<String> departamentos) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fechaCreacion = fechaCreacion;
        this.estudiantes = estudiantes;
        this.director = director;
        this.codirector = codirector;
        this.correos = correos;
        this.departamentos = departamentos;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public List<String> getEstudiantes() {
        return estudiantes;
    }

    public void setEstudiantes(List<String> estudiantes) {
        this.estudiantes = estudiantes;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getCodirector() {
        return codirector;
    }

    public void setCodirector(String codirector) {
        this.codirector = codirector;
    }

    public List<String> getCorreos() {
        return correos;
    }

    public void setCorreos(List<String> correos) {
        this.correos = correos;
    }

    public List<String> getDepartamentos() {
        return departamentos;
    }

    public void setDepartamentos(List<String> departamentos) {
        this.departamentos = departamentos;
    }
}
