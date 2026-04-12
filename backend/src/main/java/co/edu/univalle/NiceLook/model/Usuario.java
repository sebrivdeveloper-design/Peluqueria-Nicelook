package co.edu.univalle.NiceLook.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idUsuario;

    @ManyToOne
    @JoinColumn(name = "id_rol", nullable = false)
    private Rol rol;

    private String nombreCompleto;
    private String contrasena;
    private String correo;
    private String telefono;

    private LocalDateTime fechaRegistro;

    private String estado;

    // getters y setters
}