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
            "nombreCompleto", user.getNombreCompleto() != null ? user.getNombreCompleto() : "",
            "correo",         user.getCorreo() != null ? user.getCorreo() : "",
            "telefono",       user.getTelefono() != null ? user.getTelefono() : "",
            "documento",      user.getDocumento() != null ? user.getDocumento() : ""
        ));
    }

    @PutMapping("/me")
    public ResponseEntity<?> updateMe(Authentication auth, @RequestBody Map<String, String> body) {
        Usuario user = usuarioService.buscarPorCorreo(auth.getName());
        if (user == null) return ResponseEntity.notFound().build();

        String nombre = body.get("nombreCompleto");
        String telefono = body.get("telefono");

        if (nombre == null || nombre.isBlank()) {
            return ResponseEntity.badRequest().body("El nombre no puede estar vacío");
        }
        nombre = nombre.trim();
        if (nombre.length() > 100) {
            return ResponseEntity.badRequest().body("El nombre no puede superar los 100 caracteres");
        }

        if (telefono != null && !telefono.isBlank()) {
            telefono = telefono.trim();
            if (!telefono.matches("^[0-9+\\s-]{7,20}$")) {
                return ResponseEntity.badRequest().body("El teléfono solo puede contener números, espacios, + o - (7 a 20 caracteres)");
            }
            user.setTelefono(telefono);
        } else {
            user.setTelefono(null);
        }

        user.setNombreCompleto(nombre);
        usuarioService.guardar(user);

        return ResponseEntity.ok(Map.of(
            "nombreCompleto", user.getNombreCompleto(),
            "correo",         user.getCorreo() != null ? user.getCorreo() : "",
            "telefono",       user.getTelefono() != null ? user.getTelefono() : ""
        ));
    }
}
