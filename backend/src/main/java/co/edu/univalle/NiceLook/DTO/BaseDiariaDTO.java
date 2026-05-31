package co.edu.univalle.NiceLook.DTO;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
public class BaseDiariaDTO {

    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal montoInicial;
}