package co.edu.univalle.NiceLook.controller;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
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
            m.put("duracion", c.getServicio().getDuracion());
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

    // POST registrar cita
    @PostMapping
    @Transactional
    public ResponseEntity<?> registrarCita(@RequestBody RegistroCitaDTO dto) {

        try {

            Cliente cliente = clienteRepository
                    .findById(dto.getIdCliente())
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

            Empleado empleado = empleadoRepository
                    .findById(dto.getIdEmpleado())
                    .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));

            Servicio servicio = servicioRepository
                    .findById(dto.getIdServicio())
                    .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));

            Disponibilidad bloque = disponibilidadRepository
                    .findById(dto.getIdDisponibilidad())
                    .orElseThrow(() -> new RuntimeException("Bloque no encontrado"));

            if (!"disponible".equalsIgnoreCase(bloque.getEstadoBloque())) {
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body("Horario no disponible");
            }

            if (dto.getHoraInicio() == null || dto.getHoraInicio().isBlank()) {
                return ResponseEntity
                        .badRequest()
                        .body("Debes indicar la hora de inicio de la cita");
            }

            LocalTime horaInicio = LocalTime.parse(dto.getHoraInicio());
            int duracionMin = parseDuracionMinutos(servicio.getDuracion());
            LocalTime horaFin = horaInicio.plusMinutes(duracionMin);

            if (horaInicio.isBefore(bloque.getHoraInicioBloque())
                    || horaFin.isAfter(bloque.getHoraFinBloque())
                    || !horaFin.isAfter(horaInicio)) {
                return ResponseEntity
                        .badRequest()
                        .body("El servicio (" + duracionMin
                                + " min) no cabe en el horario seleccionado");
            }

            // CREAR CITA
            Cita cita = new Cita();
            cita.setCliente(cliente);
            cita.setEmpleado(empleado);
            cita.setServicio(servicio);
            cita.setFechaCita(bloque.getFecha());
            cita.setHoraInicio(horaInicio);
            cita.setHoraFin(horaFin);
            cita.setEstadoCita("pendiente");
            cita.setObservaciones(dto.getObservaciones());
            cita.setFechaCreacion(LocalDateTime.now());

            Cita citaGuardada = citaRepository.save(cita);

            dividirBloque(bloque, horaInicio, horaFin);

            // ENVIAR CORREO (simula la notificación de WhatsApp)
            try {
                emailService.enviarConfirmacionCita(citaGuardada);
            } catch (Exception e) {
                System.err.println("Error enviando correo: " + e.getMessage());
            }

            notificacionService.notificarStaff(
                    "info",
                    "Nueva cita registrada",
                    cliente.getUsuario().getNombreCompleto() + " · "
                            + servicio.getNombreServicio() + " · "
                            + bloque.getFecha() + " " + horaInicio);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body("Cita registrada exitosamente.");

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
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

        String estado = cita.getEstadoCita().toLowerCase();
        if (!estado.equals("pendiente") && !estado.equals("confirmada")) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("Solo se pueden finalizar citas pendientes o confirmadas. Estado actual: "
                            + cita.getEstadoCita());
        }

        cita.setEstadoCita("finalizada");
        citaRepository.save(cita);

        return ResponseEntity.ok("Cita finalizada. Quedó disponible para registro de pago.");
    }

    // PUT reprogramar cita (cliente)
    @PutMapping("/{id}/reprogramar")
    @Transactional
    public ResponseEntity<?> reprogramarCita(
            @PathVariable Integer id,
            @RequestBody RegistroCitaDTO dto) {

        try {

            Cita cita = citaRepository.findById(id).orElse(null);

            if (cita == null) return ResponseEntity.notFound().build();

            String estado = cita.getEstadoCita().toLowerCase();
            if (!estado.equals("pendiente") && !estado.equals("confirmada")) {
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body("No se puede reprogramar una cita " + cita.getEstadoCita() + ".");
            }

            // Menos de 1 hora de anticipación → no se permite
            LocalDateTime inicioActual = LocalDateTime.of(cita.getFechaCita(), cita.getHoraInicio());
            if (Duration.between(LocalDateTime.now(), inicioActual).toMinutes() < 60) {
                return ResponseEntity
                        .badRequest()
                        .body("Falta menos de 1 hora para la cita. Comuníquese con el establecimiento.");
            }

            // Nuevo bloque destino
            Disponibilidad bloqueNuevo = disponibilidadRepository
                    .findById(dto.getIdDisponibilidad())
                    .orElseThrow(() -> new RuntimeException("Horario no encontrado"));

            if (!"disponible".equalsIgnoreCase(bloqueNuevo.getEstadoBloque())) {
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body("El horario seleccionado ya no está disponible.");
            }

            if (dto.getHoraInicio() == null || dto.getHoraInicio().isBlank()) {
                return ResponseEntity.badRequest().body("Debes indicar la nueva hora de inicio.");
            }

            LocalTime horaInicio = LocalTime.parse(dto.getHoraInicio());
            int duracionMin = parseDuracionMinutos(cita.getServicio().getDuracion());
            LocalTime horaFin = horaInicio.plusMinutes(duracionMin);

            if (horaInicio.isBefore(bloqueNuevo.getHoraInicioBloque())
                    || horaFin.isAfter(bloqueNuevo.getHoraFinBloque())
                    || !horaFin.isAfter(horaInicio)) {
                return ResponseEntity
                        .badRequest()
                        .body("El servicio (" + duracionMin + " min) no cabe en el nuevo horario.");
            }

            // 1. Liberar el horario anterior (con fusión de bloques contiguos)
            liberarBloqueDeCita(cita);

            // 2. Ocupar el nuevo horario
            dividirBloque(bloqueNuevo, horaInicio, horaFin);

            // 3. Actualizar la cita
            cita.setFechaCita(bloqueNuevo.getFecha());
            cita.setHoraInicio(horaInicio);
            cita.setHoraFin(horaFin);
            citaRepository.save(cita);

            // Correo con la información actualizada (simula WhatsApp)
            try {
                emailService.enviarConfirmacionCita(cita);
            } catch (Exception e) {
                System.err.println("Error enviando correo: " + e.getMessage());
            }

            notificacionService.notificarStaff(
                    "info",
                    "Cita reprogramada",
                    cita.getCliente().getUsuario().getNombreCompleto() + " · "
                            + cita.getServicio().getNombreServicio() + " · "
                            + cita.getFechaCita() + " " + horaInicio);

            return ResponseEntity.ok("Cita reprogramada exitosamente.");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    // DELETE cancelar cita
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<?> cancelarCita(@PathVariable Integer id) {

        try {

            Cita cita = citaRepository.findById(id)
                    .orElse(null);

            if (cita == null) {
                return ResponseEntity.notFound().build();
            }

            String estado = cita.getEstadoCita().toLowerCase();

            if (estado.equals("cancelada")) {
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body("La cita ya estaba cancelada.");
            }

            if (estado.equals("finalizada")) {
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body("No se puede cancelar una cita finalizada.");
            }

            cita.setEstadoCita("cancelada");
            citaRepository.save(cita);

            liberarBloqueDeCita(cita);

            notificacionService.notificarStaff(
                    "warning",
                    "Cita cancelada",
                    cita.getCliente().getUsuario().getNombreCompleto() + " · "
                            + cita.getServicio().getNombreServicio() + " · "
                            + cita.getFechaCita());

            return ResponseEntity.ok("Cita cancelada exitosamente.");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    // ─────────────────────────────────────────────
    // HELPERS DE BLOQUES DE DISPONIBILIDAD
    // ─────────────────────────────────────────────

    /**
     * Divide un bloque disponible: el tramo [horaInicio, horaFin] queda ocupado
     * y los tramos sobrantes (antes/después) permanecen disponibles.
     */
    private void dividirBloque(Disponibilidad bloque, LocalTime horaInicio, LocalTime horaFin) {

        LocalTime inicioBloque = bloque.getHoraInicioBloque();
        LocalTime finBloque = bloque.getHoraFinBloque();
        Empleado empleado = bloque.getEmpleado();

        if (horaInicio.equals(inicioBloque)) {
            bloque.setHoraFinBloque(horaFin);
            bloque.setEstadoBloque("ocupado");
            disponibilidadRepository.save(bloque);
        } else {
            bloque.setHoraFinBloque(horaInicio);
            disponibilidadRepository.save(bloque);

            Disponibilidad ocupado = new Disponibilidad();
            ocupado.setEmpleado(empleado);
            ocupado.setFecha(bloque.getFecha());
            ocupado.setHoraInicioBloque(horaInicio);
            ocupado.setHoraFinBloque(horaFin);
            ocupado.setEstadoBloque("ocupado");
            disponibilidadRepository.save(ocupado);
        }

        if (horaFin.isBefore(finBloque)) {
            Disponibilidad resto = new Disponibilidad();
            resto.setEmpleado(empleado);
            resto.setFecha(bloque.getFecha());
            resto.setHoraInicioBloque(horaFin);
            resto.setHoraFinBloque(finBloque);
            resto.setEstadoBloque("disponible");
            disponibilidadRepository.save(resto);
        }
    }

    /**
     * Libera el bloque ocupado por una cita y lo fusiona con los bloques
     * disponibles contiguos (antes y después) para no fragmentar la agenda.
     */
    private void liberarBloqueDeCita(Cita cita) {

        Integer idEmpleado = cita.getEmpleado().getIdEmpleado();
        LocalDate fecha = cita.getFechaCita();

        Disponibilidad bloque = disponibilidadRepository
                .findByEmpleado_IdEmpleadoAndFechaAndHoraInicioBloqueAndHoraFinBloqueAndEstadoBloque(
                        idEmpleado,
                        fecha,
                        cita.getHoraInicio(),
                        cita.getHoraFin(),
                        "ocupado")
                .orElse(null);

        if (bloque == null) return;

        bloque.setEstadoBloque("disponible");

        List<Disponibilidad> antes = disponibilidadRepository
                .findByEmpleado_IdEmpleadoAndFechaAndHoraFinBloqueAndEstadoBloque(
                        idEmpleado, fecha,
                        bloque.getHoraInicioBloque(),
                        "disponible");

        if (!antes.isEmpty()) {
            bloque.setHoraInicioBloque(antes.get(0).getHoraInicioBloque());
            disponibilidadRepository.delete(antes.get(0));
        }

        List<Disponibilidad> despues = disponibilidadRepository
                .findByEmpleado_IdEmpleadoAndFechaAndHoraInicioBloqueAndEstadoBloque(
                        idEmpleado, fecha,
                        bloque.getHoraFinBloque(),
                        "disponible");

        if (!despues.isEmpty()) {
            bloque.setHoraFinBloque(despues.get(0).getHoraFinBloque());
            disponibilidadRepository.delete(despues.get(0));
        }

        disponibilidadRepository.save(bloque);
    }

    // La duración del servicio se guarda como texto libre ("30 min", "1 hora")
    private int parseDuracionMinutos(String duracion) {
        if (duracion == null) return 60;
        String d = duracion.toLowerCase().trim();
        java.util.regex.Matcher m = java.util.regex.Pattern.compile("\\d+").matcher(d);
        if (!m.find()) return 60;
        int valor = Integer.parseInt(m.group());
        if (d.contains("hora") || (d.contains("h") && !d.contains("min"))) {
            valor *= 60;
        }
        return Math.max(valor, 5);
    }
}
