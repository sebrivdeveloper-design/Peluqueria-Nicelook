package co.edu.univalle.NiceLook.model;
import lombok.Data;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Data
@Entity
@Getter
@Setter
@Table(name = "categoria_servicios")
public class CategoriaServicios {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria", nullable = false)
    private Long idCategoria;

    @Column(name = "nombre_categoria", nullable = false)
    private String nombreCategoria;

    @Column(name = "descripcion")
    private String descripcion;
    
    @Column(name="activo" , nullable = false)
    private Boolean activo = true;

    @Column(name = "imagen")
    private String imagen;
    // getters y setters
}
