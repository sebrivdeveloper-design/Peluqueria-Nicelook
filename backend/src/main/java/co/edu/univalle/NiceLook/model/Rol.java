package co.edu.univalle.NiceLook.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Data
@Table(name = "rol")
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol",nullable = false)
    private Integer idRol;

    @Column(name = "nombre_rol", nullable = false, unique = true)
    private String nombreRol;

    // getters y setters
}
