package co.edu.univalle.NiceLook.controller;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.univalle.NiceLook.DTO.RegistroCitaDTO;
import co.edu.univalle.NiceLook.model.Cita;
import co.edu.univalle.NiceLook.model.Cliente;
import co.edu.univalle.NiceLook.model.Disponibilidad;
import co.edu.univalle.NiceLook.model.Empleado;
import co.edu.univalle.NiceLook.model.Servicio;
import co.edu.univalle.NiceLook.repository.CitaRepository;
import co.edu.univalle.NiceLook.repository.ClienteRepository;
import co.edu.univalle.NiceLook.repository.DisponibilidadRepository;
import co.edu.univalle.NiceLook.repository.EmpleadoRepository;
import co.edu.univalle.NiceLook.repository.ServicioRepository;
import co.edu.univalle.NiceLook.service.EmailService;
import co.edu.univalle.NiceLook.service.NotificacionService;

@RestController
@RequestMapping("/api/citas")
@CrossOrigin(origins = "*")
public class CitaController {

    @Autowired private ServicioRepository servicioRepository;
    @Autowired private CitaRepository citaRepository;
    @Autowired private ClienteRepository clienteRepository;
    @Autowired private EmpleadoRepository empleadoRepository;
    @Autowired private DisponibilidadRepository disponibilidadRepository;
    @Autowired private EmailService emailService;
    @Autowired private NotificacionService notificacionService;
    @Autowired private co.edu.univalle.NiceLook.service.ReservaService reservaService;

    // GET horarios disponibles de un barbero en una fecha
    @GetMapping("/disponibilidad/{idEmpleado}")
    public ResponseEntity<?> getDisponibilidad(
            @PathVariable Integer idEmpleado,
            @RequestParam String fecha) {
        LocalDate localFecha = LocalDate.parse(fecha);
        List<Disponibilidad> bloques = disponibilidadRepository
            .findByEmpleado_IdEmpleadoAndFechaAndEstadoBloque(idEmpleado, localFecha, "disponible");
        return ResponseEntity.ok(bloques);
    }

    // GET citas de un cliente (mis citas + historial)
    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<?> citasDeCliente(@PathVariable Integer idCliente) {

        List<Cita> citas = citaRepository.findByClienteOrdenadas(idCliente);

        List<Map<String, Object>> response = citas.stream().map(c -> {
            Map<String, Object> m = new HashMap<>();
            m.put("idCita", c.getIdCita());
            m.put("fecha", c.getFechaCita().toString());
            m.put("horaInicio", c.getHoraInicio().toString());
            m.put("horaFin", c.getHoraFin().toString());
            m.put("estadoCita", c.getEstadoCita());
            m.put("servicio", c.getServicio().getNombreServicio());
            m.put("idServicio", c.getServicio().getIdServicio());
            m.put("duracion", c.getServicio().getDuracionMinutos());
            m.put("precio", c.getServicio().getPrecio());
            m.put("empleado", c.getEmpleado().getUsuario() != null
                    ? c.getEmpleado().getUsuario().getNombreCompleto() : "");
            m.put("idEmpleado", c.getEmpleado().getIdEmpleado());
            return m;
        }).toList();

        return ResponseEntity.ok(response);
    }

    // GET agenda diaria: citas de todos los barberos en una fecha
    @GetMapping("/dia")
    public ResponseEntity<?> citasDelDia(@RequestParam(required = false) String fecha) {

        LocalDate dia = (fecha == null || fecha.isBlank())
                ? LocalDate.now()
                : LocalDate.parse(fecha);

        List<Cita> citas = citaRepository.findCitasActivasPorFecha(dia);

        List<Map<String, Object>> response = citas.stream().map(c -> {
            Map<String, Object> m = new HashMap<>();
            m.put("idCita", c.getIdCita());
            m.put("horaInicio", c.getHoraInicio().toString());
            m.put("horaFin", c.getHoraFin().toString());
            m.put("estadoCita", c.getEstadoCita());
            m.put("cliente", c.getCliente().getUsuario() != null
                    ? c.getCliente().getUsuario().getNombreCompleto() : "");
            m.put("servicio", c.getServicio().getNombreServicio());
            m.put("empleado", c.getEmpleado().getUsuario() != null
                    ? c.getEmpleado().getUsuario().getNombreCompleto() : "");
            return m;
        }).toList();

        return ResponseEntity.ok(response);
    }

    // POST registrar cita (panel interno) — delega en ReservaService (lógica única)
    @PostMapping
    public ResponseEntity<?> registrarCita(@RequestBody RegistroCitaDTO dto) {
        try {
            reservaService.crearCita(dto, "panel");
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body("Cita registrada exitosamente.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // PUT confirmar asistencia (cliente)
    @PutMapping("/{id}/confirmar")
    public ResponseEntity<?> confirmarAsistencia(@PathVariable Integer id) {

        Cita cita = citaRepository.findById(id).orElse(null);

        if (cita == null) return ResponseEntity.notFound().build();

        if (!"pendiente".equalsIgnoreCase(cita.getEstadoCita())) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("Solo se pueden confirmar citas pendientes. Estado actual: "
                            + cita.getEstadoCita());
        }

        cita.setEstadoCita("confirmada");
        citaRepository.save(cita);

        return ResponseEntity.ok("Asistencia confirmada.");
    }

    // PUT finalizar cita (barbero)
    @PutMapping("/{id}/finalizar")
    public ResponseEntity<?> finalizarCita(@PathVariable Integer id) {

        Cita cita = citaRepository.findById(id).orElse(null);

        if (cita == null) return ResponseEntity.notFound().build();

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(e.getMessage());
        }
    }

// PUT cancelar cita
    @PutMapping("/{idCita}/cancelar")
    public ResponseEntity<?> cancelarCita(@PathVariable Integer idCita) {

        try {

            Cita cita = citaRepository
                .findById(idCita)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));

            if (!"pendiente".equalsIgnoreCase(cita.getEstadoCita())) {
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Solo se pueden cancelar citas pendientes.");
            }

            cita.setEstadoCita("cancelada");
            citaRepository.save(cita);

            Disponibilidad bloque = disponibilidadRepository
                .findByEmpleado_IdEmpleadoAndFechaAndHoraInicioBloque(
                    cita.getEmpleado().getIdEmpleado(),
                    cita.getFechaCita(),
                    cita.getHoraInicio()
                )
                .orElseThrow(() -> new RuntimeException("Bloque de disponibilidad no encontrado"));

            bloque.setEstadoBloque("disponible");
            disponibilidadRepository.save(bloque);

            return ResponseEntity.ok("Cita cancelada exitosamente.");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(e.getMessage());
        }
    }
}
