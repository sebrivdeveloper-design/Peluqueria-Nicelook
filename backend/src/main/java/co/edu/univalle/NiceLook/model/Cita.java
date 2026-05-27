package co.edu.univalle.NiceLook.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

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

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @ManyToOne (fetch = FetchType.LAZY)
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
    // getters y setters
}