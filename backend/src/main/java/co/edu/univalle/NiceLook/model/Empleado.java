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

    @Column(name = "salario")
    private Double salario;

    @Column(name = "estado_laboral", nullable = false)
    private String estadoLaboral;

    // getters y setters
}

