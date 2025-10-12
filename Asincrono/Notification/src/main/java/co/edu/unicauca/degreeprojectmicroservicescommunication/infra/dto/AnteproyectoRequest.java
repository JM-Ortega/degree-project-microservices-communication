package co.edu.unicauca.degreeprojectmicroservicescommunication.infra.dto;

public class AnteproyectoRequest {
    private String titulo;
    private String descripcion;
    private String estudiante1;
    private String estudiante2;
    private String director;
    private String codirector;
    private String departamento;

    public AnteproyectoRequest(String codirector, String departamento, String descripcion, String director, String estudiante1, String estudiante2, String titulo) {
        this.codirector = codirector;
        this.departamento = departamento;
        this.descripcion = descripcion;
        this.director = director;
        this.estudiante1 = estudiante1;
        this.estudiante2 = estudiante2;
        this.titulo = titulo;
    }

    public String getCodirector() {
        return codirector;
    }

    public void setCodirector(String codirector) {
        this.codirector = codirector;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

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

    public String getEstudiante1() {
        return estudiante1;
    }

    public void setEstudiante1(String estudiante1) {
        this.estudiante1 = estudiante1;
    }

    public String getEstudiante2() {
        return estudiante2;
    }

    public void setEstudiante2(String estudiante2) {
        this.estudiante2 = estudiante2;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
}
