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
import co.edu.univalle.NiceLook.model.Cliente;
import co.edu.univalle.NiceLook.model.Usuario;
import co.edu.univalle.NiceLook.repository.ClienteRepository;
import co.edu.univalle.NiceLook.service.GoogleAuthService;
import co.edu.univalle.NiceLook.service.UsuarioService;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired private JwtService jwtService;
    @Autowired private GoogleAuthService googleAuthService;
    @Autowired private UsuarioService usuarioService;
    @Autowired private ClienteRepository clienteRepository;

    @PostMapping("/google")
    public ResponseEntity<?> loginWithGoogle(@RequestBody TokenRequest request) {
        try {
            GoogleUser googleUser = googleAuthService.verifyToken(request.getToken());
            Usuario user = usuarioService.buscarPorCorreo(googleUser.getEmail());

            if (user == null) {
                return ResponseEntity.status(404)
                        .body("El usuario no se encuentra registrado");
            }

            System.out.println("ROL DEL USUARIO: " + user.getRol());

            // Buscar idCliente si el rol es CLIENTE
            Integer idCliente = null;
            String rolNombre = user.getRol().getNombreRol();
            if (rolNombre.equals("CLIENTE")) {
                Cliente cliente = clienteRepository
                    .findByUsuario_Documento(user.getDocumento())
                    .orElse(null);
                if (cliente != null) {
                    idCliente = cliente.getIdCliente();
                }
            }

            String token = jwtService.generateToken(user, idCliente);
            return ResponseEntity.ok(token);

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("Error al autenticar con Google");
        }
    }
}