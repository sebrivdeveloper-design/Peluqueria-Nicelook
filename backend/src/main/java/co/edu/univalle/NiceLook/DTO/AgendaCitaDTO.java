package co.edu.univalle.NiceLook.DTO;

import java.time.LocalDate;
import java.time.LocalTime;

public class AgendaCitaDTO {

    private LocalDate fechaCita;
    private LocalTime horaInicio;
    private String cliente;
    private String servicio;

    public AgendaCitaDTO(
            LocalDate fechaCita,
            LocalTime horaInicio,
            String cliente,
            String servicio) {

        this.fechaCita = fechaCita;
        this.horaInicio = horaInicio;
        this.cliente = cliente;
        this.servicio = servicio;
    }

    public LocalDate getFechaCita() {
        return fechaCita;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public String getCliente() {
        return cliente;
    }

    public String getServicio() {
        return servicio;
    }
}