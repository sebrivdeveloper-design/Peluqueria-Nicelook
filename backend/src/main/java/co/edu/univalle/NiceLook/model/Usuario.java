package co.edu.univalle.NiceLook.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario", nullable = false)
    private Integer idUsuario;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "id_rol", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Rol rol;

    // Nuevo campo para mapear la cédula/ID que exige tu interfaz de Vue
    @Column(name = "documento", nullable = false, unique = true)
    private String documento;

    @Column(name = "nombre_completo", nullable = false)
    private String nombreCompleto;

    @Column(nullable = false, unique = true)
    private String correo;

    private String telefono; 

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;

    @Column(name = "estado", nullable = false)
    private String estado;

    private Boolean esGoogleUser;
}