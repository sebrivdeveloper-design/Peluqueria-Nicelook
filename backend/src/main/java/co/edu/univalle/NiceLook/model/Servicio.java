package co.edu.univalle.NiceLook.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Table(name = "servicio")
public class Servicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_servicio", nullable = false)
    private Integer idServicio;

    @ManyToOne
    @JoinColumn(name = "id_categoria", nullable = false)
    private CategoriaServicios categoria;

    @NotBlank
    @Size(max = 30)
    @Column(name = "nombre_servicio", nullable = false)
    private String nombreServicio;

    @NotBlank
    @Size(max = 100)
    @Column(name = "descripcion", nullable = false)
    private String descripcion;

    // CAMBIO IMPORTANTE
    @NotBlank
    @Size(max = 30)
    @Column(name = "duracion", nullable = false)
    private String duracion;

    // CAMBIO PRO
    @NotNull
    @Digits(integer = 8, fraction = 2)
    @Column(name = "precio", nullable = false)
    private BigDecimal precio;

    @Column(name = "estado", nullable = false)
    private String estado = "activo";
}