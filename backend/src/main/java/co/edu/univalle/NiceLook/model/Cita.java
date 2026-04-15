package co.edu.univalle.NiceLook.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.*;

@Entity
@Getter
@Setter
@Table(name = "cita")
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCita;

    @ManyToOne
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "id_empleado")
    private Empleado empleado;

    private LocalDate fechaCita;
    private LocalTime horaInicio;
    private LocalTime horaFin;

    private String estadoCita;
    private String observaciones;

    private LocalDateTime fechaCreacion;

    // getters y setters
}