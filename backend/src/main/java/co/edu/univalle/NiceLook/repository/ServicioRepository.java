package co.edu.univalle.NiceLook.repository;

import co.edu.univalle.NiceLook.model.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServicioRepository extends JpaRepository<Servicio, Integer> {
    // Aquí ya tenemos métodos como findAll(), save(), etc.
}