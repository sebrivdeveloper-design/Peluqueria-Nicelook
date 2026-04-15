package co.edu.univalle.NiceLook.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Data
@Table(name = "servicio")
public class Servicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_servicio", nullable = false)
    private Integer idServicio;

    @ManyToOne
    @JoinColumn(name = "id_categoria", nullable = false)
    private CategoriaServicios categoria;

    @Column(name = "nombre_servicio", nullable = false)
    private String nombreServicio;

    @Column(name = "descripcion", nullable = false)
    private String descripcion;

    @Column(name = "duracion", nullable = false)
    private Integer duracion;

    @Column(name = "precio", nullable = false)
    private Double precio;

    @Column(name = "estado", nullable = false)
    private String estado;

    // getters y setters
}
