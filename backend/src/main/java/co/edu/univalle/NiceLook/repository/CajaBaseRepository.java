package co.edu.univalle.NiceLook.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.univalle.NiceLook.model.CajaBase;

@Repository
public interface CajaBaseRepository extends JpaRepository<CajaBase, Integer> {

    Optional<CajaBase> findByFecha(LocalDate fecha);
}
