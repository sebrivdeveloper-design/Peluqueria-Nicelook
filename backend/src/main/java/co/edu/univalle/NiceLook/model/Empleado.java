package co.edu.univalle.NiceLook.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "empleado")
public class Empleado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idEmpleado;

    @OneToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    private String documento;
    private LocalDate fechaIngreso;
    private String especialidad;
    private Double salario;
    private String estadoLaboral;

    // getters y setters
}

