package co.edu.univalle.NiceLook.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HistorialCitaDTO {

    private Integer idCita;
    private String fecha;
    private String horaInicio;
    private String horaFin;
    private String servicio;
    private String barbero;
    private String estado;
}