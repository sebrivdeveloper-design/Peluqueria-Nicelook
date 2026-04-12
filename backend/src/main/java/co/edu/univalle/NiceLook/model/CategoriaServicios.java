package co.edu.univalle.NiceLook.model;

import jakarta.persistence.*;

@Entity
@Table(name = "categoria_servicios")
public class CategoriaServicios {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCategoria;

    private String nombreCategoria;
    private String descripcion;

    // getters y setters
}
