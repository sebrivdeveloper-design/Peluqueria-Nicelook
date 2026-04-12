package co.edu.univalle.NiceLook.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "pago")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPago;

    @OneToOne
    @JoinColumn(name = "id_cita")
    private Cita cita;

    private Double montoTotal;
    private String metodoPago;
    private String referencia;
    private String estadoPago;
    private LocalDateTime fechaPago;

    // getters y setters
}

