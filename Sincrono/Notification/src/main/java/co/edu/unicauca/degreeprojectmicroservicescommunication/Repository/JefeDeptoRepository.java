package co.edu.unicauca.degreeprojectmicroservicescommunication.Repository;

import co.edu.unicauca.degreeprojectmicroservicescommunication.Entities.Departamento;
import co.edu.unicauca.degreeprojectmicroservicescommunication.Entities.JefeDepto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositorio para gestionar las operaciones CRUD de los jefes de departamento.
 * Extiende JpaRepository para aprovechar los métodos de persistencia estándar.
 */
public interface JefeDeptoRepository extends JpaRepository<JefeDepto,Long> {
    /**
     * Busca un jefe de departamento según el departamento especificado.
     * @param depto el departamento del cual se desea obtener el jefe
     * @return un {@link Optional} que contiene el {@link JefeDepto} si existe,
     * o vacío si no se encuentra ningún jefe para ese departamento
     */
    Optional<JefeDepto> findByDepto(Departamento depto);
}
