package co.edu.univalle.NiceLook.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.univalle.NiceLook.model.Notificacion;
import co.edu.univalle.NiceLook.repository.NotificacionRepository;

@Service
public class NotificacionService {

    @Autowired
    private NotificacionRepository notificacionRepository;

    public void crear(String rolDestino, String tipo, String titulo, String mensaje) {
        Notificacion n = new Notificacion();
        n.setRolDestino(rolDestino);
        n.setTipo(tipo);
        n.setTitulo(titulo);
        n.setMensaje(mensaje);
        n.setLeida(false);
        n.setFecha(LocalDateTime.now());
        notificacionRepository.save(n);
    }

    // Genera la notificación para ADMIN y RECEPCIONISTA (HU-42)
    public void notificarStaff(String tipo, String titulo, String mensaje) {
        crear("ADMIN", tipo, titulo, mensaje);
        crear("RECEPCIONISTA", tipo, titulo, mensaje);
    }
}
