package co.edu.univalle.NiceLook.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "base_diaria")
public class BaseDiaria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_base", nullable = false)
    private Integer idBase;

    @NotNull
    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @NotNull
    @Digits(integer = 8, fraction = 2)
    @Column(name = "monto_inicial", nullable = false)
    private BigDecimal montoInicial;
}