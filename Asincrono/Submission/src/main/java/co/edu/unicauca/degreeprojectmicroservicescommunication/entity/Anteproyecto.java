package co.edu.unicauca.degreeprojectmicroservicescommunication.entity;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "anteproyecto")
public class Anteproyecto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String descripcion;

    @Temporal(TemporalType.DATE)
    private Date fechaCreacion;

    @OneToOne(mappedBy = "anteproyecto", cascade = CascadeType.ALL)
    private TrabajoDeGrado trabajoDeGrado;

    public Anteproyecto() {}

    // Getters y setters
    public Long getId() { return id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Date getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(Date fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public TrabajoDeGrado getTrabajoDeGrado() { return trabajoDeGrado; }
    public void setTrabajoDeGrado(TrabajoDeGrado trabajoDeGrado) { this.trabajoDeGrado = trabajoDeGrado; }
}
