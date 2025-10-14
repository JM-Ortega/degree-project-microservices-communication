package co.edu.unicauca.degreeprojectmicroservicescommunication.Repository;

import co.edu.unicauca.degreeprojectmicroservicescommunication.Entities.Departamento;
import co.edu.unicauca.degreeprojectmicroservicescommunication.Entities.JefeDepto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Repositorio para gestionar las operaciones CRUD de los jefes de departamento.
 * Extiende JpaRepository para aprovechar los métodos de persistencia estándar.
 */
public interface JefeDeptoRepository extends JpaRepository<JefeDepto,Long> {
    /**
     * Busca todos los jefes de departamento cuyos departamentos se encuentran en el conjunto especificado.
     * @param departamentos conjunto de departamentos para los cuales se desea obtener los jefes asociados
     * @return una lista de {@link JefeDepto} que pertenecen a los departamentos indicados;
     * la lista puede estar vacía si no se encuentra ningún jefe para los departamentos dados
     */
    @Query("SELECT j FROM JefeDepto j WHERE j.depto IN :departamentos")
    List<JefeDepto> findByDeptos(@Param("departamentos") Set<Departamento> departamentos);
}
