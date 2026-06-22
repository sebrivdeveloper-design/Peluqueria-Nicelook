package co.edu.univalle.NiceLook.DTO;

public class ClienteDTO {

    private Integer idCliente;
    private String documento;
    private String nombreCompleto;
    private String telefono;
    private String correo;

    public ClienteDTO() {}

    public ClienteDTO(
        Integer idCliente,
        String documento,
        String nombreCompleto,
        String telefono,
        String correo
    ) {
        this.idCliente = idCliente;
        this.documento = documento;
        this.nombreCompleto = nombreCompleto;
        this.telefono = telefono;
        this.correo = correo;
    }

    // GETTERS Y SETTERS

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    private String genero;
    private String fechaNacimiento;
    private String observaciones;
    private String estado;

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}