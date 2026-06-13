package co.edu.univalle.NiceLook.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.univalle.NiceLook.DTO.ClienteDTO;
import co.edu.univalle.NiceLook.DTO.RegistroClienteDTO;
import co.edu.univalle.NiceLook.model.Cliente;
import co.edu.univalle.NiceLook.model.Rol;
import co.edu.univalle.NiceLook.model.Usuario;
import co.edu.univalle.NiceLook.repository.ClienteRepository;
import co.edu.univalle.NiceLook.repository.RolRepository;
import co.edu.univalle.NiceLook.repository.UsuarioRepository;

@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "*")
public class ClienteController {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    // GET: http://localhost:8080/api/clientes
    @GetMapping
    public ResponseEntity<List<ClienteDTO>> listarClientes() {
        List<Cliente> clientes = clienteRepository.findAll();

        List<ClienteDTO> dtos = clientes.stream().map(c -> {
            ClienteDTO dto = new ClienteDTO();
            dto.setIdCliente(c.getIdCliente());
            dto.setGenero(c.getGenero());
            dto.setFechaNacimiento(c.getFechaNacimiento() != null
                    ? c.getFechaNacimiento().toString() : "");
            dto.setObservaciones(c.getObservaciones());
            if (c.getUsuario() != null) {
                dto.setDocumento(c.getUsuario().getDocumento());
                dto.setNombreCompleto(c.getUsuario().getNombreCompleto());
                dto.setTelefono(c.getUsuario().getTelefono());
                dto.setCorreo(c.getUsuario().getCorreo());
                dto.setEstado(c.getUsuario().getEstado());
            } else {
                dto.setDocumento("S/D");
                dto.setNombreCompleto("CLIENTE SIN USUARIO ASIGNADO");
            }
            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    // POST: http://localhost:8080/api/clientes
    @PostMapping
    public ResponseEntity<?> registrarCliente(@RequestBody RegistroClienteDTO dto) {

        // Verificar si ya existe correo o teléfono
        if (usuarioRepository.findByCorreo(dto.getCorreo()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("Ya existe un cliente registrado con ese correo electrónico.");
        }

        if (usuarioRepository.findByTelefono(dto.getTelefono()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("Ya existe un cliente registrado con ese teléfono.");
        }

        // Rol CLIENTE = id 3
        Rol rolCliente = rolRepository.findById(3).orElseThrow();

        // Crear Usuario
        Usuario usuario = new Usuario();
        usuario.setDocumento(dto.getDocumento());
        usuario.setNombreCompleto(dto.getNombreCompleto());
        usuario.setTelefono(dto.getTelefono());
        usuario.setCorreo(dto.getCorreo());
        usuario.setRol(rolCliente);
        usuario.setEstado("activo");
        usuario.setFechaRegistro(LocalDateTime.now());
        usuario.setEsGoogleUser(false);
        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        // Crear Cliente
        Cliente cliente = new Cliente();
        cliente.setUsuario(usuarioGuardado);
        cliente.setGenero(dto.getGenero());
        cliente.setFechaNacimiento(LocalDate.parse(dto.getFechaNacimiento()));
        cliente.setObservaciones(dto.getObservaciones());
        clienteRepository.save(cliente);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body("Cliente registrado exitosamente.");
    }

    // PUT: editar datos de un cliente (HU-35)
    @PutMapping("/{id}")
    public ResponseEntity<?> editarCliente(
            @PathVariable Integer id,
            @RequestBody RegistroClienteDTO dto) {

        try {

            Cliente cliente = clienteRepository.findById(id).orElse(null);
            if (cliente == null || cliente.getUsuario() == null) {
                return ResponseEntity.notFound().build();
            }

            Usuario usuario = cliente.getUsuario();

            // Validaciones de campos
            if (dto.getNombreCompleto() == null || dto.getNombreCompleto().isBlank()) {
                return ResponseEntity.badRequest().body("El nombre es obligatorio.");
            }
            if (dto.getNombreCompleto().length() > 100) {
                return ResponseEntity.badRequest().body("El nombre no puede superar 100 caracteres.");
            }
            if (dto.getTelefono() == null || dto.getTelefono().isBlank()) {
                return ResponseEntity.badRequest().body("El teléfono es obligatorio.");
            }
            if (!dto.getTelefono().matches("^[0-9]{1,12}$")) {
                return ResponseEntity.badRequest().body("El teléfono debe ser numérico (máx. 12 dígitos).");
            }
            if (dto.getCorreo() == null || dto.getCorreo().isBlank()) {
                return ResponseEntity.badRequest().body("El correo es obligatorio.");
            }
            if (dto.getCorreo().length() > 50) {
                return ResponseEntity.badRequest().body("El correo no puede superar 50 caracteres.");
            }

            // Duplicados: el correo/teléfono no pueden pertenecer a OTRO usuario
            var conCorreo = usuarioRepository.findByCorreo(dto.getCorreo());
            if (conCorreo.isPresent() && !conCorreo.get().getIdUsuario().equals(usuario.getIdUsuario())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Ese correo ya pertenece a otro cliente.");
            }

            var conTelefono = usuarioRepository.findByTelefono(dto.getTelefono());
            if (conTelefono.isPresent() && !conTelefono.get().getIdUsuario().equals(usuario.getIdUsuario())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Ese teléfono ya pertenece a otro cliente.");
            }

            usuario.setNombreCompleto(dto.getNombreCompleto().trim());
            usuario.setTelefono(dto.getTelefono().trim());
            usuario.setCorreo(dto.getCorreo().trim());
            if (dto.getDocumento() != null && !dto.getDocumento().isBlank()) {
                usuario.setDocumento(dto.getDocumento().trim());
            }
            usuarioRepository.save(usuario);

            if (dto.getGenero() != null && !dto.getGenero().isBlank()) {
                cliente.setGenero(dto.getGenero());
            }
            if (dto.getFechaNacimiento() != null && !dto.getFechaNacimiento().isBlank()) {
                cliente.setFechaNacimiento(LocalDate.parse(dto.getFechaNacimiento()));
            }
            if (dto.getObservaciones() != null) {
                cliente.setObservaciones(dto.getObservaciones());
            }
            clienteRepository.save(cliente);

            return ResponseEntity.ok("Cliente actualizado exitosamente.");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("No se pudo actualizar el cliente: " + e.getMessage());
        }
    }

    // PUT: desactivar cliente (HU-45) — se oculta de búsquedas activas y no puede iniciar sesión
    @PutMapping("/{id}/desactivar")
    public ResponseEntity<?> desactivarCliente(@PathVariable Integer id) {

        Cliente cliente = clienteRepository.findById(id).orElse(null);
        if (cliente == null || cliente.getUsuario() == null) {
            return ResponseEntity.notFound().build();
        }

        cliente.getUsuario().setEstado("inactivo");
        usuarioRepository.save(cliente.getUsuario());

        return ResponseEntity.ok("Cliente desactivado. Su información e historial se conservan.");
    }

    // PUT: reactivar cliente
    @PutMapping("/{id}/activar")
    public ResponseEntity<?> activarCliente(@PathVariable Integer id) {

        Cliente cliente = clienteRepository.findById(id).orElse(null);
        if (cliente == null || cliente.getUsuario() == null) {
            return ResponseEntity.notFound().build();
        }

        cliente.getUsuario().setEstado("activo");
        usuarioRepository.save(cliente.getUsuario());

        return ResponseEntity.ok("Cliente reactivado exitosamente.");
    }
}