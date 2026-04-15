package co.edu.univalle.NiceLook.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idUsuario;

    @ManyToOne
    @JoinColumn(name = "id_rol", nullable = false)
    private Rol rol;

    private String nombreCompleto;

    @Column(nullable = false, unique = true)
    private String correo;

    private String telefono;

    private LocalDateTime fechaRegistro;

    private String estado;

    private Boolean esGoogleUser;

    // getters y setters
}