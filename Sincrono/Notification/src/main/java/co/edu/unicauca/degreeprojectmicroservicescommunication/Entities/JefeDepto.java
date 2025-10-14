package co.edu.unicauca.degreeprojectmicroservicescommunication.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Entity
public class JefeDepto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String fullName;

    @Email
    @NotBlank
    @Column(unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    private Departamento depto;

    public JefeDepto() {
    } // requerido por JPA

    // getters/setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Departamento getDepto() {
        return depto;
    }

    public void setDepto(Departamento depto) {
        this.depto = depto;
    }
}
