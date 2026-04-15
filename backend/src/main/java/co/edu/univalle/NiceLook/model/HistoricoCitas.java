package co.edu.univalle.NiceLook.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "historico_citas")
public class HistoricoCitas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idHistorial;

    @ManyToOne
    @JoinColumn(name = "id_cita", nullable = false)
    private Cita cita;

    @ManyToOne
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "id_empleado", nullable = false)
    private Empleado empleado;

    private String estadoFinal;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    // getters y setters
}

