package co.edu.univalle.NiceLook.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Tarifa de arrendamiento (uso del salón) que el estilista paga por servicio realizado.
 *
 * El modelo de negocio reemplaza el salario fijo por una comisión basada en arriendo:
 * de cada servicio cobrado, el salón retiene el valor de arrendamiento y el resto es
 * para el estilista.
 *
 * Una sola tabla cubre los tres niveles de configuración. La especificidad se deduce
 * de qué columnas están en NULL:
 *   - empleado = NULL  y  servicio = NULL  -> valor por defecto del salón.
 *   - empleado != NULL y  servicio = NULL  -> tarifa general de ese estilista.
 *   - empleado != NULL y  servicio != NULL -> tarifa de esa combinación estilista+servicio.
 *
 * La resolución (de más específico a más general) vive en {@code ArrendamientoService}.
 */
@Entity
@Getter
@Setter
@Table(name = "arrendamiento")
public class Arrendamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_arrendamiento", nullable = false)
    private Integer idArrendamiento;

    // NULL = aplica a todos los estilistas (valor por defecto)
    @ManyToOne
    @JoinColumn(name = "id_empleado")
    private Empleado empleado;

    // NULL = aplica a todos los servicios de ese estilista (tarifa general del estilista)
    @ManyToOne
    @JoinColumn(name = "id_servicio")
    private Servicio servicio;

    @Column(name = "valor", nullable = false)
    private BigDecimal valor;

    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;
}
