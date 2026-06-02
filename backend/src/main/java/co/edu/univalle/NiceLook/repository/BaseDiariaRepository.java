package co.edu.univalle.NiceLook.repository;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

import co.edu.univalle.NiceLook.model.BaseDiaria;

@Repository
public interface BaseDiariaRepository
        extends JpaRepository<BaseDiaria, Integer> {

    boolean existsByFecha(LocalDate fecha);
    Optional<BaseDiaria> findByFecha(LocalDate fecha);

}