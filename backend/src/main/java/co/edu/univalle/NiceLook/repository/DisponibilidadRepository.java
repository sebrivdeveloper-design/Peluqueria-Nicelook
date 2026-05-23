package co.edu.univalle.NiceLook.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import co.edu.univalle.NiceLook.model.Disponibilidad;

@Repository
public interface DisponibilidadRepository
        extends JpaRepository<Disponibilidad, Integer> {

    List<Disponibilidad>
    findByEmpleado_IdEmpleadoAndFechaAndEstadoBloque(
        Integer idEmpleado,
        LocalDate fecha,
        String estadoBloque
    );

    List<Disponibilidad>
    findByFechaAndEstadoBloque(
        LocalDate fecha,
        String estadoBloque
    );

    @Query("""
        SELECT d
        FROM Disponibilidad d
        WHERE d.empleado.idEmpleado = :idEmpleado
        AND d.fecha BETWEEN :inicio AND :fin
    """)
    List<Disponibilidad> findByEmpleadoAndFechaBetween(
        @Param("idEmpleado") Integer idEmpleado,
        @Param("inicio") LocalDate inicio,
        @Param("fin") LocalDate fin
    );

    // 🔥 VALIDAR CRUCE DE HORARIOS
    boolean existsByEmpleado_IdEmpleadoAndFechaAndHoraInicioBloqueLessThanAndHoraFinBloqueGreaterThan(
        Integer idEmpleado,
        LocalDate fecha,
        LocalTime horaFin,
        LocalTime horaInicio
    );
}