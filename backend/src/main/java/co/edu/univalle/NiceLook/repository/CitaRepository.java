package co.edu.univalle.NiceLook.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import co.edu.univalle.NiceLook.model.Cita;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Integer> {

    // Verifica si un barbero ya tiene cita en ese horario
    @Query("SELECT COUNT(c) > 0 FROM Cita c WHERE c.empleado.idEmpleado = :idEmpleado " +
           "AND c.fechaCita = :fecha AND c.horaInicio = :horaInicio " +
           "AND c.estadoCita != 'cancelada'")
    boolean existeCitaEnHorario(@Param("idEmpleado") Integer idEmpleado,
                                 @Param("fecha") LocalDate fecha,
                                 @Param("horaInicio") LocalTime horaInicio);

    // Citas del día siguiente para el scheduler de recordatorios
    @Query("SELECT c FROM Cita c WHERE c.fechaCita = :fecha AND c.estadoCita = 'pendiente'")
    List<Cita> findCitasPorFecha(@Param("fecha") LocalDate fecha);
}