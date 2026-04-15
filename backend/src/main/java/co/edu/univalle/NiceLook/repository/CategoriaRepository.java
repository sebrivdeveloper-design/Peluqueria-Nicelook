package co.edu.univalle.NiceLook.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import co.edu.univalle.NiceLook.model.CategoriaServicios;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<CategoriaServicios, Long> {
    
    Optional<CategoriaServicios> findByNombreCategoria(String nombreCategoria);
    List<CategoriaServicios> findByActivoTrue();
}