package co.edu.univalle.NiceLook.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.univalle.NiceLook.model.Arrendamiento;

@Repository
public interface ArrendamientoRepository extends JpaRepository<Arrendamiento, Integer> {

    // Nivel 1: combinación estilista + servicio
    Optional<Arrendamiento> findByEmpleado_IdEmpleadoAndServicio_IdServicio(Integer idEmpleado, Integer idServicio);

    // Nivel 2: tarifa general del estilista (sin servicio específico)
    Optional<Arrendamiento> findByEmpleado_IdEmpleadoAndServicioIsNull(Integer idEmpleado);

    // Nivel 3: valor por defecto del salón
    Optional<Arrendamiento> findByEmpleadoIsNullAndServicioIsNull();

    // Listado ordenado para el panel de administración
    List<Arrendamiento> findAllByOrderByEmpleado_IdEmpleadoAscServicio_IdServicioAsc();
}
