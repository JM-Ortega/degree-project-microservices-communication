package co.edu.unicauca.degreeprojectmicroservicescommunication.infra.dto;

import java.util.Date;
import java.util.List;

public class AnteproyectoRequest {
    private String titulo;
    private String descripcion;
    private Date fechaCreacion;
    private List<String> estudiantes;
    private String director;
    private String codirector;
    private List<String> correos;
    private List<String> departamentos;

    public AnteproyectoRequest(String codirector, List<String> correos, List<String> departamentos, String descripcion, String director, List<String> estudiantes, Date fechaCreacion, String titulo) {
        this.codirector = codirector;
        this.correos = correos;
        this.departamentos = departamentos;
        this.descripcion = descripcion;
        this.director = director;
        this.estudiantes = estudiantes;
        this.fechaCreacion = fechaCreacion;
        this.titulo = titulo;
    }

    public String getCodirector() {
        return codirector;
    }

    public void setCodirector(String codirector) {
        this.codirector = codirector;
    }

    public List<String> getCorreos() {return correos;}

    public void setCorreos(List<String> correos) {this.correos = correos;}

    public List<String> getDepartamentos() {return departamentos;}

    public void setDepartamentos(List<String> departamentos) {this.departamentos = departamentos;}

    public List<String> getEstudiantes() {return estudiantes;}

    public void setEstudiantes(List<String> estudiantes) {this.estudiantes = estudiantes;}

    public Date getFechaCreacion() {return fechaCreacion;}

    public void setFechaCreacion(Date fechaCreacion) {this.fechaCreacion = fechaCreacion;}

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
}
