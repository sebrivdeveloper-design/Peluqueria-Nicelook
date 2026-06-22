package co.edu.univalle.NiceLook.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "notificacion")
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notificacion", nullable = false)
    private Integer idNotificacion;

    // Rol al que va dirigida la notificación: ADMIN o RECEPCIONISTA
    @Column(name = "rol_destino", nullable = false)
    private String rolDestino;

    @Column(name = "tipo", nullable = false)
    private String tipo;

    @Column(name = "titulo", nullable = false)
    private String titulo;

    @Column(name = "mensaje", nullable = false, length = 500)
    private String mensaje;

    @Column(name = "leida", nullable = false)
    private Boolean leida = false;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;
}
