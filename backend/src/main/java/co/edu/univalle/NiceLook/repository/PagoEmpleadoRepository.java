package co.edu.univalle.NiceLook.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import co.edu.univalle.NiceLook.model.PagoEmpleado;

@Repository
public interface PagoEmpleadoRepository extends JpaRepository<PagoEmpleado, Integer> {

    @Query("""
                SELECT p
                FROM PagoEmpleado p
                WHERE p.empleado.idEmpleado = :idEmpleado
                AND p.periodoInicio >= :desde
                AND p.periodoFin <= :hasta
                ORDER BY p.fechaPago DESC
            """)
    List<PagoEmpleado> findByEmpleadoYRango(
            @Param("idEmpleado") Integer idEmpleado,
            @Param("desde") LocalDate desde,
            @Param("hasta") LocalDate hasta);

    List<PagoEmpleado> findByEmpleado_IdEmpleadoOrderByFechaPagoDesc(Integer idEmpleado);
}
