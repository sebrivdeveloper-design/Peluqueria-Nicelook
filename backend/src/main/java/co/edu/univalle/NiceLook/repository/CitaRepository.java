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

    @Query("""
                SELECT COUNT(c) > 0
                FROM Cita c
                WHERE c.empleado.idEmpleado = :idEmpleado
                AND c.fechaCita = :fecha
                AND c.horaInicio = :horaInicio
                AND c.horaFin = :horaFin
                AND c.estadoCita != 'cancelada'
            """)
    boolean existeCitaEnBloque(
            @Param("idEmpleado") Integer idEmpleado,
            @Param("fecha") LocalDate fecha,
            @Param("horaInicio") LocalTime horaInicio,
            @Param("horaFin") LocalTime horaFin);

    @Query("""
                SELECT c
                FROM Cita c
                WHERE c.empleado.idEmpleado = :idEmpleado
                AND c.fechaCita = :fecha
                AND c.horaInicio = :horaInicio
                AND c.estadoCita != 'cancelada'
            """)
    Cita findByEmpleadoAndHorario(
            @Param("idEmpleado") Integer idEmpleado,
            @Param("fecha") LocalDate fecha,
            @Param("horaInicio") LocalTime horaInicio);

    // Citas de un cliente, más recientes primero
    @Query("""
                SELECT c
                FROM Cita c
                WHERE c.cliente.idCliente = :idCliente
                ORDER BY c.fechaCita DESC, c.horaInicio DESC
            """)
    List<Cita> findByClienteOrdenadas(@Param("idCliente") Integer idCliente);

    // Agenda diaria: citas no canceladas de todos los barberos en una fecha
    @Query("""
                SELECT c
                FROM Cita c
                WHERE c.fechaCita = :fecha
                AND c.estadoCita != 'cancelada'
                ORDER BY c.horaInicio ASC
            """)
    List<Cita> findCitasActivasPorFecha(@Param("fecha") LocalDate fecha);

    // Citas finalizadas de una fecha (para registro de pagos)
    @Query("""
                SELECT c
                FROM Cita c
                WHERE c.fechaCita = :fecha
                AND c.estadoCita = 'finalizada'
                ORDER BY c.horaInicio ASC
            """)
    List<Cita> findFinalizadasPorFecha(@Param("fecha") LocalDate fecha);

    // Citas finalizadas de un empleado en un rango (para pago a empleados)
    @Query("""
                SELECT c
                FROM Cita c
                WHERE c.empleado.idEmpleado = :idEmpleado
                AND c.estadoCita = 'finalizada'
                AND c.fechaCita BETWEEN :desde AND :hasta
            """)
    List<Cita> findFinalizadasPorEmpleadoEntre(
            @Param("idEmpleado") Integer idEmpleado,
            @Param("desde") LocalDate desde,
            @Param("hasta") LocalDate hasta);

    // Citas finalizadas en un rango (para reportes)
    @Query("""
                SELECT c
                FROM Cita c
                WHERE c.estadoCita = 'finalizada'
                AND c.fechaCita BETWEEN :desde AND :hasta
            """)
    List<Cita> findFinalizadasEntre(
            @Param("desde") LocalDate desde,
            @Param("hasta") LocalDate hasta);
}