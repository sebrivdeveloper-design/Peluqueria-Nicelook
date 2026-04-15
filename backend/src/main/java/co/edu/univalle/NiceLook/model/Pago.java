package co.edu.univalle.NiceLook.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Data
@Entity
@Getter
@Setter
@Table(name = "pago")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago", nullable = false)
    private Integer idPago;

    @OneToOne
    @JoinColumn(name = "id_cita", nullable = false)
    private Cita cita;

    @Column(name = "monto_total", nullable = false)
    private Double montoTotal;
    
    @Column(name = "metodo_pago", nullable = false)
    private String metodoPago;
    
    @Column(name = "referencia", nullable = false)
    private String referencia;
    
    @Column(name = "estado_pago", nullable = false)
    private String estadoPago;
    
    @Column(name = "fecha_pago", nullable = false)
    private LocalDateTime fechaPago;

    // getters y setters
}

