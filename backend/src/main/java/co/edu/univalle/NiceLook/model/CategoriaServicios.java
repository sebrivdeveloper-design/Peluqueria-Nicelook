package co.edu.univalle.NiceLook.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "categoria_servicios")
public class CategoriaServicios {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCategoria;

    private String nombreCategoria;
    private String descripcion;

    // getters y setters
}
