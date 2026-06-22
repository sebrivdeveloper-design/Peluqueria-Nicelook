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
import co.edu.univalle.NiceLook.repository.EmpleadoRepository;
import co.edu.univalle.NiceLook.service.GoogleAuthService;
import co.edu.univalle.NiceLook.service.UsuarioService;
import co.edu.univalle.NiceLook.model.Empleado;
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    @Autowired private EmpleadoRepository empleadoRepository;
    @Autowired private JwtService jwtService;
    @Autowired private GoogleAuthService googleAuthService;
    @Autowired private UsuarioService usuarioService;
    @Autowired private ClienteRepository clienteRepository;
    @Autowired private co.edu.univalle.NiceLook.repository.RolRepository rolRepository;

    @PostMapping("/google")
    public ResponseEntity<?> loginWithGoogle(@RequestBody TokenRequest request) {
        try {
            GoogleUser googleUser = googleAuthService.verifyToken(request.getToken());
            Usuario user = usuarioService.buscarPorCorreo(googleUser.getEmail());

            if (user == null) {
                return ResponseEntity.status(404)
                        .body("El usuario no se encuentra registrado");
            }

            if (user.getEstado() != null && !user.getEstado().equalsIgnoreCase("activo")) {
                return ResponseEntity.status(403)
                        .body("Tu cuenta está desactivada. Comuníquese con el establecimiento.");
            }

            System.out.println("ROL DEL USUARIO: " + user.getRol());

            if (user.getRol() == null) {
                return ResponseEntity.status(500)
                        .body("El usuario no tiene un rol asignado");
            }

            Integer idCliente = null;
Integer idEmpleado = null;

String rolNombre = user.getRol().getNombreRol();

// CLIENTE
if (rolNombre.equals("CLIENTE")) {

    Cliente cliente = clienteRepository
        .findByUsuario_Documento(user.getDocumento())
        .orElse(null);

    if (cliente != null) {
        idCliente = cliente.getIdCliente();
    }
}

// BARBERO-EMPLEADO
if (rolNombre.equals("EMPLEADO")) {

    Empleado empleado = empleadoRepository
        .findByUsuario_Documento(user.getDocumento())
        .orElse(null);

    if (empleado != null) {
        idEmpleado = empleado.getIdEmpleado();
    }
}

String token = jwtService.generateToken(user, idCliente, idEmpleado);
            return ResponseEntity.ok(token);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body("Error al autenticar con Google: " + e.getMessage());
        }
    }

    // Login/registro de CLIENTES: si la cuenta de Google no existe, se registra automáticamente
    @PostMapping("/google/cliente")
    public ResponseEntity<?> loginClienteWithGoogle(@RequestBody TokenRequest request) {
        try {
            GoogleUser googleUser = googleAuthService.verifyToken(request.getToken());
            Usuario user = usuarioService.buscarPorCorreo(googleUser.getEmail());

            boolean nuevo = false;

            if (user == null) {

                // AUTO-REGISTRO como CLIENTE (HU-33)
                co.edu.univalle.NiceLook.model.Rol rolCliente =
                        rolRepository.findByNombreRol("CLIENTE")
                                .orElseGet(() -> rolRepository.findById(3).orElseThrow());

                user = new Usuario();
                user.setDocumento("G-" + System.currentTimeMillis());
                user.setNombreCompleto(googleUser.getNombre() != null
                        ? googleUser.getNombre() : googleUser.getEmail());
                user.setCorreo(googleUser.getEmail());
                user.setRol(rolCliente);
                user.setEstado("activo");
                user.setFechaRegistro(java.time.LocalDateTime.now());
                user.setEsGoogleUser(true);
                user = usuarioService.guardar(user);

                Cliente cliente = new Cliente();
                cliente.setUsuario(user);
                cliente.setGenero("otro");
                cliente.setFechaNacimiento(java.time.LocalDate.of(2000, 1, 1));
                clienteRepository.save(cliente);

                nuevo = true;
            }

            if (user.getEstado() != null && !user.getEstado().equalsIgnoreCase("activo")) {
                return ResponseEntity.status(403)
                        .body("Tu cuenta está desactivada. Comuníquese con el establecimiento.");
            }

            if (user.getRol() == null || !user.getRol().getNombreRol().equals("CLIENTE")) {
                // Cuentas de staff deben usar el login normal
                return ResponseEntity.status(403)
                        .body("Esta cuenta pertenece al personal. Usa el inicio de sesión principal.");
            }

            Integer idCliente = clienteRepository
                    .findByUsuario_Documento(user.getDocumento())
                    .map(Cliente::getIdCliente)
                    .orElse(null);

            String token = jwtService.generateToken(user, idCliente, null);

            return ResponseEntity.ok(java.util.Map.of(
                    "token", token,
                    "nuevo", nuevo,
                    "telefono", user.getTelefono() != null ? user.getTelefono() : ""));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body("Error al autenticar con Google: " + e.getMessage());
        }
    }
}