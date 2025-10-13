package co.edu.unicauca.degreeprojectmicroservicescommunication.repository;

import co.edu.unicauca.degreeprojectmicroservicescommunication.entity.Estudiante;
import co.edu.unicauca.degreeprojectmicroservicescommunication.entity.TrabajoDeGrado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {
    Optional<Estudiante> findByCorreo(String correo);

    @Query("SELECT t FROM TrabajoDeGrado t JOIN t.estudiantes e WHERE e.correo = :correo")
    List<TrabajoDeGrado> findTrabajosByEstudianteCorreo(@Param("correo") String correo);
}
