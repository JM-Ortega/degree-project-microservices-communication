package co.edu.unicauca.degreeprojectmicroservicescommunication.infra.dto;

import java.util.List;

public class AnteproyectoRequest {
    private String titulo;
    private String descripcion;

    private List<EstudianteDTO> estudiantes;
    private DocenteDTO director;
    private DocenteDTO codirector;
    private String departamento;


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

    public List<EstudianteDTO> getEstudiantes() {
        return estudiantes;
    }

    public void setEstudiantes(List<EstudianteDTO> estudiantes) {
        this.estudiantes = estudiantes;
    }

    public DocenteDTO getDirector() {
        return director;
    }

    public void setDirector(DocenteDTO director) {
        this.director = director;
    }

    public DocenteDTO getCodirector() {
        return codirector;
    }

    public void setCodirector(DocenteDTO codirector) {
        this.codirector = codirector;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }
}
