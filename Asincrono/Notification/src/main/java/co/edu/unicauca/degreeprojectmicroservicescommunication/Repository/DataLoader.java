package co.edu.unicauca.degreeprojectmicroservicescommunication.Repository;

import co.edu.unicauca.degreeprojectmicroservicescommunication.Entities.Departamento;
import co.edu.unicauca.degreeprojectmicroservicescommunication.Entities.JefeDepto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Componente encargado de inicializar datos en el repositorio de jefes de departamento.
 */
@Component
public class DataLoader implements CommandLineRunner {
    @Autowired
    JefeDeptoRepository jefeDeptoRepository;

    /**
     * Ejecuta la carga inicial de datos en el repositorio de jefes de departamento.
     * Elimina todos los registros existentes en el repositorio y crea nuevamente
     * las instancias de los jefes de departamento con su información predeterminada.
     * @param args argumentos de la línea de comandos
     * @throws Exception Exception si ocurre algún error durante la inicialización de los datos
     */
    @Override
    public void run(String... args) throws Exception {
        jefeDeptoRepository.deleteAll();

        JefeDepto jefe1 = new JefeDepto();
        jefe1.setFullName("Néstor Milciades Díaz Mariño");
        jefe1.setDepto(Departamento.SISTEMAS);
        jefe1.setEmail("nediaz@unicauca.edu.co");
        jefeDeptoRepository.save(jefe1);

        JefeDepto jefe2 = new JefeDepto();
        jefe2.setFullName("Alvaro René Restrepo Garcés");
        jefe2.setDepto(Departamento.ELECTRONICA_INSTRUMENTACION_Y_CONTROL);
        jefe2.setEmail("arestrepo@unicauca.edu.co");
        jefeDeptoRepository.save(jefe2);

        JefeDepto jefe3 = new JefeDepto();
        jefe3.setFullName("Andrés Lara Silva");
        jefe3.setDepto(Departamento.TELEMATICA);
        jefe3.setEmail("alara@unicauca.edu.co");
        jefeDeptoRepository.save(jefe3);

        JefeDepto jefe4 = new JefeDepto();
        jefe4.setFullName("Alejandro Toledo Tovar");
        jefe4.setDepto(Departamento.TELECOMUNICACIONES);
        jefe4.setEmail("atoledo@unicauca.edu.co");
        jefeDeptoRepository.save(jefe4);
    }
}