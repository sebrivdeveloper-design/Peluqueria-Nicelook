package co.edu.univalle.NiceLook.DTO;

public class DisponibilidadDTO {
    private Integer idEmpleado;
    private String fecha;
    private String horaInicioBloque;
    private String horaFinBloque;
    private String estadoBloque;

    public Integer getIdEmpleado() { return idEmpleado; }
    public void setIdEmpleado(Integer idEmpleado) { this.idEmpleado = idEmpleado; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getHoraInicioBloque() { return horaInicioBloque; }
    public void setHoraInicioBloque(String horaInicioBloque) { this.horaInicioBloque = horaInicioBloque; }

    public String getHoraFinBloque() { return horaFinBloque; }
    public void setHoraFinBloque(String horaFinBloque) { this.horaFinBloque = horaFinBloque; }

    public String getEstadoBloque() { return estadoBloque; }
    public void setEstadoBloque(String estadoBloque) { this.estadoBloque = estadoBloque; }
}