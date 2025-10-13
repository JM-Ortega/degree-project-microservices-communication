package co.edu.unicauca.degreeprojectmicroservicescommunication.dto;

public class DocenteDTO {
    private String nombre;
    private String correo;
    private String departamento;

    // Getters y setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }
}
