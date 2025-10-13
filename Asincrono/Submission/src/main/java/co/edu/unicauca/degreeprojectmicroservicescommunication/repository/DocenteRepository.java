package co.edu.unicauca.degreeprojectmicroservicescommunication.repository;

import co.edu.unicauca.degreeprojectmicroservicescommunication.entity.Docente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DocenteRepository extends JpaRepository<Docente, Long> {
    Optional<Docente> findByCorreo(String correo);
}
