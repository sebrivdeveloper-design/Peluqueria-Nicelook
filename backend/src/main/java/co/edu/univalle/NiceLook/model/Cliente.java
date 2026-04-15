package co.edu.univalle.NiceLook.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "cliente")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCliente;

    @OneToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    private LocalDate fechaNacimiento;

    private String observaciones;

    private String genero;

    // getters y setters
}
