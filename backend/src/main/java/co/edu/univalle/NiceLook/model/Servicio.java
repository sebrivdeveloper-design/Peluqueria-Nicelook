package co.edu.univalle.NiceLook.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "servicio")
public class Servicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idServicio;

    @ManyToOne
    @JoinColumn(name = "id_categoria")
    private CategoriaServicios categoria;

    private String nombreServicio;
    private String descripcion;
    private Integer duracion;
    private Double precio;
    private String estado;

    // getters y setters
}
