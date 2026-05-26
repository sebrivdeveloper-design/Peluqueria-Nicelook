package co.edu.univalle.NiceLook.DTO;

public class AgendaEmpleadoDTO {

    private Integer idDisponibilidad;

    private String fecha;

    private String horaInicio;

    private String horaFin;

    private String estado;

    private String cliente;

    private String servicio;

    public Integer getIdDisponibilidad() {
        return idDisponibilidad;
    }

    public String getFecha() {
        return fecha;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public String getHoraFin() {
        return horaFin;
    }

    public String getEstado() {
        return estado;
    }

    public String getCliente() {
        return cliente;
    }

    public String getServicio() {
        return servicio;
    }
    public void setIdDisponibilidad(Integer idDisponibilidad) {
        this.idDisponibilidad = idDisponibilidad;
    }
    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
    public void setHoraInicio(String horaInicio) {
        this.horaInicio = horaInicio;
    }
    public void setHoraFin(String horaFin) {
        this.horaFin = horaFin;
    }
    public void setEstado(String estado) {
        this.estado = estado;
    }
    public void setCliente(String cliente) {
        this.cliente = cliente;
    }
    public void setServicio(String servicio) {
        this.servicio = servicio;
    }
}