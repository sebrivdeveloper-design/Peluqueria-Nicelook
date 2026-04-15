package co.edu.univalle.NiceLook.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.univalle.NiceLook.DTO.GoogleUser;
import co.edu.univalle.NiceLook.DTO.TokenRequest;
import co.edu.univalle.NiceLook.Service.GoogleAuthService;
import co.edu.univalle.NiceLook.Service.UsuarioService;
import co.edu.univalle.NiceLook.model.Usuario;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private GoogleAuthService googleAuthService;

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/google")
    public ResponseEntity<?> loginWithGoogle(@RequestBody TokenRequest request) {

        try {

            // Validar token
            GoogleUser googleUser = googleAuthService.verifyToken(request.getToken());

            // Buscar usuario
            Usuario user = usuarioService.buscarPorCorreo(googleUser.getEmail());

            // Usuario no existe
            if (user == null) {
                return ResponseEntity.status(404)
                        .body("El usuario no se encuentra registrado");
            }

            // Retornar usuario (incluye rol)
            return ResponseEntity.ok(user);

        } catch (Exception e) {

            return ResponseEntity.status(500)
                    .body("Error al autenticar con Google");
        }
    }
}
