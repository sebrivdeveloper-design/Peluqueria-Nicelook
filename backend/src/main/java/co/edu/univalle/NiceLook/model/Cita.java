package co.edu.univalle.NiceLook.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.*;
@Data
@Entity
@Getter
@Setter
@Table(name = "cita")
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cita", nullable = false)
    private Integer idCita;

    @ManyToOne
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "id_empleado", nullable = false)
    private Empleado empleado;

    @Column(name = "fecha_cita", nullable = false)
    private LocalDate fechaCita;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;

    @Column(name = "estado_cita", nullable = false)
    private String estadoCita;
    
    @Column(name = "observaciones")
    private String observaciones;
    
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @ManyToOne
    @JoinColumn(name = "id_servicio", nullable = false)
    private Servicio servicio;

    // ─────────────────────────────────────────────────────────────
    // Comisión por arrendamiento (CHANGE 1 - V2)
    // Estos valores se calculan y CONGELAN cuando la cita se cobra
    // (ver PagoController.registrarPago). Son históricos e inmutables:
    // no se recalculan aunque cambien las tarifas de arrendamiento.
    // Nullable: solo las citas ya cobradas los tienen.
    // ─────────────────────────────────────────────────────────────

    @Column(name = "valor_servicio")
    private BigDecimal valorServicio;

    @Column(name = "valor_arrendamiento")
    private BigDecimal valorArrendamiento;

    @Column(name = "valor_a_pagar_estilista")
    private BigDecimal valorAPagarEstilista;

    @Column(name = "valor_para_salon")
    private BigDecimal valorParaSalon;
    // getters y setters
}