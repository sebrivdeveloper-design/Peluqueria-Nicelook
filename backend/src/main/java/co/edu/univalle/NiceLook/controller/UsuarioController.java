package co.edu.univalle.NiceLook.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.univalle.NiceLook.model.Usuario;
import co.edu.univalle.NiceLook.service.UsuarioService;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/me")
    public ResponseEntity<?> getMe(Authentication auth) {
        Usuario user = usuarioService.buscarPorCorreo(auth.getName());
        if (user == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(Map.of(
            "idUsuario",      user.getIdUsuario(),
            "nombreCompleto", user.getNombreCompleto(),
            "correo",         user.getCorreo(),
            "telefono",       user.getTelefono() != null ? user.getTelefono() : "",
            "documento",      user.getDocumento()
        ));
    }

    @PutMapping("/me")
    public ResponseEntity<?> updateMe(Authentication auth, @RequestBody Map<String, String> body) {
        Usuario user = usuarioService.buscarPorCorreo(auth.getName());
        if (user == null) return ResponseEntity.notFound().build();

        String nombre = body.get("nombreCompleto");
        String telefono = body.get("telefono");

        if (nombre != null && !nombre.isBlank()) user.setNombreCompleto(nombre.trim());
        if (telefono != null) user.setTelefono(telefono.trim());

        usuarioService.guardar(user);

        return ResponseEntity.ok(Map.of(
            "nombreCompleto", user.getNombreCompleto(),
            "correo",         user.getCorreo(),
            "telefono",       user.getTelefono() != null ? user.getTelefono() : ""
        ));
    }
}
