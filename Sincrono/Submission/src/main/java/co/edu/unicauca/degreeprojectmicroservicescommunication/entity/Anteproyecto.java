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
    // Importa si usas el mismo enum que en Notification
    @Enumerated(EnumType.STRING)
    private Departamento departamento;

    public Anteproyecto() {
    }

    @PrePersist
    void prePersist() {
        if (fechaCreacion == null) {
            fechaCreacion = new Date(); // evita null al persistir
        }
    }

    // Getters y setters
    public Long getId() {
        return id;
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

    public TrabajoDeGrado getTrabajoDeGrado() {
        return trabajoDeGrado;
    }

    public void setTrabajoDeGrado(TrabajoDeGrado trabajoDeGrado) {
        this.trabajoDeGrado = trabajoDeGrado;
    }
}
