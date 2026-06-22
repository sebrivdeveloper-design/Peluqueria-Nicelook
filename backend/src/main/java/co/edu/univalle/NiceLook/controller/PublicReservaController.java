package co.edu.univalle.NiceLook.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.univalle.NiceLook.DTO.RegistroCitaDTO;
import co.edu.univalle.NiceLook.model.Cita;
import co.edu.univalle.NiceLook.model.Cliente;
import co.edu.univalle.NiceLook.model.Disponibilidad;
import co.edu.univalle.NiceLook.model.Rol;
import co.edu.univalle.NiceLook.model.Usuario;
import co.edu.univalle.NiceLook.repository.ClienteRepository;
import co.edu.univalle.NiceLook.repository.DisponibilidadRepository;
import co.edu.univalle.NiceLook.repository.EmpleadoRepository;
import co.edu.univalle.NiceLook.repository.RolRepository;
import co.edu.univalle.NiceLook.repository.ServicioRepository;
import co.edu.univalle.NiceLook.repository.UsuarioRepository;
import co.edu.univalle.NiceLook.service.OtpService;
import co.edu.univalle.NiceLook.service.ReservaService;

/**
 * Landing pública de reservas (CHANGE 4 - V2). Rutas SIN autenticación
 * (ver SecurityConfig: /api/public/** permitAll). El cliente agenda sin crear cuenta;
 * la identidad se verifica con un OTP por correo antes de confirmar la cita.
 */
@RestController
@RequestMapping("/api/public")
@CrossOrigin(origins = "*")
public class PublicReservaController {

    private static final int ROL_CLIENTE = 3;

    @Autowired private ServicioRepository servicioRepository;
    @Autowired private EmpleadoRepository empleadoRepository;
    @Autowired private DisponibilidadRepository disponibilidadRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private ClienteRepository clienteRepository;
    @Autowired private RolRepository rolRepository;
    @Autowired private OtpService otpService;
    @Autowired private ReservaService reservaService;

    // ── Catálogo: servicios activos ──
    @GetMapping("/servicios")
    public ResponseEntity<?> servicios() {
        List<Map<String, Object>> response = servicioRepository.findByEstado("activo").stream()
                .map(s -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("idServicio", s.getIdServicio());
                    m.put("nombreServicio", s.getNombreServicio());
                    m.put("descripcion", s.getDescripcion());
                    m.put("duracion", s.getDuracion());
                    m.put("precio", s.getPrecio());
                    m.put("categoria", s.getCategoria() != null ? s.getCategoria().getNombreCategoria() : "");
                    return m;
                }).toList();
        return ResponseEntity.ok(response);
    }

    // ── Estilistas activos (para "elegir estilista") ──
    @GetMapping("/estilistas")
    public ResponseEntity<?> estilistas() {
        List<Map<String, Object>> response = empleadoRepository.findByUsuarioRolNombreRol("EMPLEADO").stream()
                .filter(e -> "activo".equalsIgnoreCase(e.getEstadoLaboral()))
                .map(e -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("idEmpleado", e.getIdEmpleado());
                    m.put("nombre", e.getUsuario() != null ? e.getUsuario().getNombreCompleto() : "");
                    m.put("especialidad", e.getEspecialidad());
                    return m;
                }).toList();
        return ResponseEntity.ok(response);
    }

    // ── Disponibilidad real de un estilista en una fecha (mismos bloques que el panel) ──
    @GetMapping("/disponibilidad")
    public ResponseEntity<?> disponibilidad(
            @RequestParam Integer idEmpleado,
            @RequestParam String fecha) {
        List<Disponibilidad> bloques = disponibilidadRepository
                .findByEmpleado_IdEmpleadoAndFechaAndEstadoBloque(idEmpleado, LocalDate.parse(fecha), "disponible");
        return ResponseEntity.ok(bloques);
    }

    // ── Solicitar OTP por correo ──
    @PostMapping("/otp")
    public ResponseEntity<?> solicitarOtp(@RequestBody Map<String, String> body) {
        try {
            String correo = body.getOrDefault("correo", "").trim();
            String nombre = body.getOrDefault("nombre", "").trim();
            if (!correo.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
                return ResponseEntity.badRequest().body("Ingresa un correo electrónico válido.");
            }
            otpService.solicitar(correo, nombre);
            return ResponseEntity.ok("Código enviado a tu correo.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("No se pudo enviar el código. Intenta de nuevo.");
        }
    }

    // ── Confirmar reserva: verifica OTP, reutiliza/crea cliente y crea la cita ──
    @PostMapping("/reservas")
    public ResponseEntity<?> reservar(@RequestBody Map<String, Object> body) {
        try {
            String nombre = str(body.get("nombre"));
            String correo = str(body.get("correo"));
            String telefono = str(body.get("telefono"));
            String codigo = str(body.get("codigo"));

            if (nombre.isBlank() || correo.isBlank() || telefono.isBlank()) {
                return ResponseEntity.badRequest().body("Completa nombre, correo y teléfono.");
            }

            // 1) Verificar OTP (lanza con mensaje claro si es inválido/expirado)
            otpService.verificar(correo, codigo);

            // 2) Reutilizar o crear el cliente (sin duplicar registros)
            Cliente cliente = obtenerOCrearCliente(nombre, correo, telefono);

            // 3) Crear la cita con la lógica única (misma validación que el panel)
            RegistroCitaDTO dto = new RegistroCitaDTO();
            dto.setIdCliente(cliente.getIdCliente());
            dto.setIdEmpleado(parseInt(body.get("idEmpleado")));
            dto.setIdServicio(parseInt(body.get("idServicio")));
            dto.setIdDisponibilidad(parseInt(body.get("idDisponibilidad")));
            dto.setHoraInicio(str(body.get("horaInicio")));
            dto.setObservaciones(str(body.get("observaciones")));

            Cita cita = reservaService.crearCita(dto, "landing");

            Map<String, Object> resumen = new HashMap<>();
            resumen.put("mensaje", "¡Reserva confirmada!");
            resumen.put("servicio", cita.getServicio().getNombreServicio());
            resumen.put("estilista", cita.getEmpleado().getUsuario() != null
                    ? cita.getEmpleado().getUsuario().getNombreCompleto() : "");
            resumen.put("fecha", cita.getFechaCita().toString());
            resumen.put("horaInicio", cita.getHoraInicio().toString());
            resumen.put("horaFin", cita.getHoraFin().toString());
            resumen.put("precio", cita.getServicio().getPrecio());
            return ResponseEntity.status(HttpStatus.CREATED).body(resumen);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ───────────────────────── helpers ─────────────────────────

    private Cliente obtenerOCrearCliente(String nombre, String correo, String telefono) {
        // Reutiliza por correo o teléfono si ya existe un cliente
        Optional<Cliente> existente = clienteRepository.findByUsuario_Correo(correo);
        if (existente.isEmpty()) existente = clienteRepository.findByUsuario_Telefono(telefono);
        if (existente.isPresent()) return existente.get();

        // Si el correo pertenece a un usuario que NO es cliente (p. ej. staff), no lo tocamos
        if (usuarioRepository.findByCorreo(correo).isPresent()) {
            throw new RuntimeException("Ese correo ya está registrado en el sistema. Usa otro o contacta al salón.");
        }

        Rol rolCliente = rolRepository.findById(ROL_CLIENTE)
                .orElseThrow(() -> new RuntimeException("Rol de cliente no configurado."));

        Usuario usuario = new Usuario();
        usuario.setNombreCompleto(nombre);
        usuario.setCorreo(correo);
        usuario.setTelefono(telefono);
        // documento es obligatorio/único en BD; se genera uno sintético para reservas web
        usuario.setDocumento("WEB-" + System.currentTimeMillis());
        usuario.setRol(rolCliente);
        usuario.setEstado("activo");
        usuario.setFechaRegistro(LocalDateTime.now());
        usuario.setEsGoogleUser(false);
        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        Cliente cliente = new Cliente();
        cliente.setUsuario(usuarioGuardado);
        // genero y fecha_nacimiento son NOT NULL en BD: se usan valores centinela que el
        // Admin puede completar luego (HU-35). Ver CHANGES_V2.md (limitaciones).
        cliente.setGenero("otro");
        cliente.setFechaNacimiento(LocalDate.of(1900, 1, 1));
        return clienteRepository.save(cliente);
    }

    private String str(Object o) {
        return o == null ? "" : o.toString().trim();
    }

    private Integer parseInt(Object o) {
        if (o == null || o.toString().isBlank()) return null;
        return Integer.valueOf(o.toString().trim());
    }
}
