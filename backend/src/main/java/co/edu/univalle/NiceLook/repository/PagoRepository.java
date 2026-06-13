package co.edu.univalle.NiceLook.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import co.edu.univalle.NiceLook.model.Pago;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Integer> {

    List<Pago> findByFechaPagoBetweenOrderByFechaPagoDesc(LocalDateTime desde, LocalDateTime hasta);

    boolean existsByCita_IdCitaAndEstadoPago(Integer idCita, String estadoPago);

    @Query("""
        SELECT COALESCE(SUM(p.montoTotal), 0)
        FROM Pago p
        WHERE p.fechaPago BETWEEN :desde AND :hasta
        AND p.estadoPago = 'completado'
    """)
    Double totalCompletadosEntre(
        @Param("desde") LocalDateTime desde,
        @Param("hasta") LocalDateTime hasta
    );
}
