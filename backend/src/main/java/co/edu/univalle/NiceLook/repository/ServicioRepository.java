package co.edu.univalle.NiceLook.repository;

import co.edu.univalle.NiceLook.model.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServicioRepository extends JpaRepository<Servicio, Integer> {

    List<Servicio> findByCategoria_IdCategoria(Long idCategoria);

}