package co.edu.univalle.NiceLook.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.univalle.NiceLook.DTO.GoogleUser;
import co.edu.univalle.NiceLook.DTO.TokenRequest;
import co.edu.univalle.NiceLook.Segurity.JwtService;
import co.edu.univalle.NiceLook.service.GoogleAuthService;
import co.edu.univalle.NiceLook.service.UsuarioService;
import co.edu.univalle.NiceLook.model.Usuario;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private JwtService jwtService;

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
            // 🔥 DEBUG
            System.out.println("ROL DEL USUARIO: " + user.getRol());

            // Retornar usuario (incluye rol)
            String token = jwtService.generateToken(user);

            return ResponseEntity.ok(token);

        } catch (Exception e) {

            return ResponseEntity.status(500)
                    .body("Error al autenticar con Google");
        }
    }
}
