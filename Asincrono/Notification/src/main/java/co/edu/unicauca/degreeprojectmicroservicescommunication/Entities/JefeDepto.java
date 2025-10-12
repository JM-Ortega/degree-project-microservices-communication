package co.edu.unicauca.degreeprojectmicroservicescommunication.Entities;

import jakarta.persistence.*;

@Entity
public class JefeDepto {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long Id;
    private String fullName;
    private String email;
    private String password;
    private Departamento depto;

    public Departamento getDepto() {
        return depto;
    }
    public void setDepto(Departamento depto) {
        this.depto = depto;
    }
    public String getEmail() {return email;}
    public void setEmail(String email) {this.email = email;}
    public String getFullName() {return fullName;}
    public void setFullName(String fullName) {this.fullName = fullName;}
    public Long getId() {return Id;}
    public void setId(Long id) {Id = id;}
    public String getPassword() {return password;}
    public void setPassword(String password) {this.password = password;}

}
