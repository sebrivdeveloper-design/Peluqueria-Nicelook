package co.edu.univalle.NiceLook.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "detalle_pago_empleado")
public class DetallePagoEmpleado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle", nullable = false)
    private Integer idDetalle;

    @ManyToOne
    @JoinColumn(name = "id_pago_empleado", nullable = false)
    @JsonIgnore
    private PagoEmpleado pagoEmpleado;

    @ManyToOne
    @JoinColumn(name = "id_servicio", nullable = false)
    private Servicio servicio;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "subtotal", nullable = false)
    private BigDecimal subtotal;
}
