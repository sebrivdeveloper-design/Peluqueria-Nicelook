package co.edu.univalle.NiceLook.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HistorialCitaDTO {

    private String fecha;
    private String servicio;
    private String barbero;
    private String estado;
}