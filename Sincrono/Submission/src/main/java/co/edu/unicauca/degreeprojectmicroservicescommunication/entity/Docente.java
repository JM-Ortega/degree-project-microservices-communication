package co.edu.unicauca.degreeprojectmicroservicescommunication.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "docente")
public class Docente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String correo;
    private String departamento;

    public Docente() {}

    public Docente(String nombre, String correo, String departamento) {
        this.nombre = nombre;
        this.correo = correo;
        this.departamento = departamento;
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }
}
