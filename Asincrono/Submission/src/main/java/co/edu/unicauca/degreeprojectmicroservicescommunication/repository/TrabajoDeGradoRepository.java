package co.edu.unicauca.degreeprojectmicroservicescommunication.repository;

import co.edu.unicauca.degreeprojectmicroservicescommunication.entity.TrabajoDeGrado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TrabajoDeGradoRepository extends JpaRepository<TrabajoDeGrado, Long> {
    @Query("SELECT COUNT(t) FROM TrabajoDeGrado t WHERE t.director.correo = :correo OR t.codirector.correo = :correo")
    Long countTrabajosByProfesor(@Param("correo") String correo);
}
