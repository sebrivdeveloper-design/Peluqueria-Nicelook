package co.edu.univalle.NiceLook.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.univalle.NiceLook.model.Servicio;

@Repository
public interface ServicioRepository extends JpaRepository<Servicio, Integer> {

    List<Servicio> findByCategoria_IdCategoria(Long idCategoria);
    List<Servicio> findByEstado(String estado);
    List<Servicio> findByNombreServicioContainingIgnoreCase(String nombreServicio); //Buscar nombre del servicio
    List<Servicio> findByNombreServicioContainingIgnoreCaseAndEstado(String nombreServicio, String estado);

}