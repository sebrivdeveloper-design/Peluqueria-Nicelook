package co.edu.univalle.NiceLook.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
@Data
@Entity
@Getter
@Setter
@Table(name = "disponibilidad")
public class Disponibilidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_disponibilidad")
    private Integer idDisponibilidad;

    @ManyToOne
    @JoinColumn(name = "id_empleado", nullable = false)
    private Empleado empleado;

    @Column(name = "fecha")
    private LocalDate fecha;

    @Column(name = "hora_inicio_bloque")
    private LocalTime horaInicioBloque;

    @Column(name = "hora_fin_bloque")
    private LocalTime horaFinBloque;

    @Column(name = "estado_bloque")
    private String estadoBloque;

    // getters y setters

}