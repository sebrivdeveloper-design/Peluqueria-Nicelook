package co.edu.univalle.NiceLook.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.univalle.NiceLook.model.Notificacion;
import co.edu.univalle.NiceLook.repository.NotificacionRepository;
import co.edu.univalle.NiceLook.repository.UsuarioRepository;

@RestController
@RequestMapping("/api/notificaciones")
@CrossOrigin(origins = "*")
public class NotificacionController {

    @Autowired private NotificacionRepository notificacionRepository;
    @Autowired private UsuarioRepository usuarioRepository;

    private String rolDe(Authentication auth) {
        if (auth == null) return null;
        var usuario = usuarioRepository.findByCorreo(auth.getName()).orElse(null);
        if (usuario == null || usuario.getRol() == null) return null;
        return usuario.getRol().getNombreRol();
    }

    // GET notificaciones del rol del usuario autenticado
    @GetMapping
    public ResponseEntity<?> listar(Authentication auth) {

        String rol = rolDe(auth);
        if (rol == null) return ResponseEntity.ok(List.of());

        List<Notificacion> lista = notificacionRepository.findByRolDestinoOrderByFechaDesc(rol);

        List<Map<String, Object>> response = lista.stream().map(n -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", n.getIdNotificacion());
            m.put("tipo", n.getTipo());
            m.put("titulo", n.getTitulo());
            m.put("mensaje", n.getMensaje());
            m.put("leida", n.getLeida());
            m.put("fecha", n.getFecha().toString());
            return m;
        }).toList();

        return ResponseEntity.ok(response);
    }

    // PUT marcar todas como leídas
    @PutMapping("/leidas")
    public ResponseEntity<?> marcarLeidas(Authentication auth) {

        String rol = rolDe(auth);
        if (rol == null) return ResponseEntity.ok().build();

        List<Notificacion> noLeidas = notificacionRepository.findByRolDestinoAndLeidaFalse(rol);
        noLeidas.forEach(n -> n.setLeida(true));
        notificacionRepository.saveAll(noLeidas);

        return ResponseEntity.ok("Notificaciones marcadas como leídas.");
    }
}
