package co.edu.unicauca.degreeprojectmicroservicescommunication.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "trabajo_de_grado")
public class TrabajoDeGrado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Un trabajo de grado puede tener 1 o 2 estudiantes
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "trabajo_estudiantes",
            joinColumns = @JoinColumn(name = "trabajo_id"),
            inverseJoinColumns = @JoinColumn(name = "estudiante_id")
    )
    private List<Estudiante> estudiantes;

    // Un trabajo de grado tiene un director obligatorio
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "director_id")
    private Docente director;

    // Puede tener un codirector opcional
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "codirector_id")
    private Docente codirector;

    @OneToOne
    @JoinColumn(name = "anteproyecto_id")
    private Anteproyecto anteproyecto;

    public TrabajoDeGrado() {}

    public Long getId() { return id; }

    public List<Estudiante> getEstudiantes() { return estudiantes; }
    public void setEstudiantes(List<Estudiante> estudiantes) { this.estudiantes = estudiantes; }

    public Docente getDirector() { return director; }
    public void setDirector(Docente director) { this.director = director; }

    public Docente getCodirector() { return codirector; }
    public void setCodirector(Docente codirector) { this.codirector = codirector; }

    public Anteproyecto getAnteproyecto() { return anteproyecto; }
    public void setAnteproyecto(Anteproyecto anteproyecto) { this.anteproyecto = anteproyecto; }
}
