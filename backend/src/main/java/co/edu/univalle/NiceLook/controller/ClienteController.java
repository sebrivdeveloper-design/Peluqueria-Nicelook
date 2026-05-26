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
import org.springframework.web.bind.annotation.PostMapping;
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
            if (c.getUsuario() != null) {
                dto.setDocumento(c.getUsuario().getDocumento());
                dto.setNombreCompleto(c.getUsuario().getNombreCompleto());
                dto.setTelefono(c.getUsuario().getTelefono());
                dto.setCorreo(c.getUsuario().getCorreo());
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
}