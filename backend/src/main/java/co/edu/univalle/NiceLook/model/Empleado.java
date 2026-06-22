package co.edu.univalle.NiceLook.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Data
@Table(name = "empleado")
public class Empleado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_empleado" , nullable = false)
    private Integer idEmpleado;

    @OneToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(name = "documento", nullable = false)
    private String documento;

    @Column(name = "fecha_ingreso", nullable = false)
    private LocalDate fechaIngreso;

    @Column(name = "especialidad")
    private String especialidad;

    /**
     * @deprecated (CHANGE 1 - V2) El negocio ya no maneja salario fijo mensual.
     * Los estilistas son independientes y el salón cobra una comisión por
     * arrendamiento por servicio (ver {@link Arrendamiento}). La columna se conserva
     * para no perder datos históricos, pero ya no se usa ni se muestra en la UI.
     */
    @Deprecated
    @Column(name = "salario")
    private Double salario;

    @Column(name = "estado_laboral", nullable = false)
    private String estadoLaboral;

    // getters y setters
}

