package co.edu.univalle.NiceLook.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.univalle.NiceLook.DTO.RegistroCitaDTO;
import co.edu.univalle.NiceLook.model.Cita;
import co.edu.univalle.NiceLook.model.Cliente;
import co.edu.univalle.NiceLook.model.Disponibilidad;
import co.edu.univalle.NiceLook.model.Empleado;
import co.edu.univalle.NiceLook.repository.CitaRepository;
import co.edu.univalle.NiceLook.repository.ClienteRepository;
import co.edu.univalle.NiceLook.repository.DisponibilidadRepository;
import co.edu.univalle.NiceLook.repository.EmpleadoRepository;
import co.edu.univalle.NiceLook.repository.ServicioRepository;
import co.edu.univalle.NiceLook.service.EmailService;
import co.edu.univalle.NiceLook.model.Servicio;

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

    // POST registrar cita
    @PostMapping
    public ResponseEntity<?> registrarCita(@RequestBody RegistroCitaDTO dto) {

        try {

            // BUSCAR CLIENTE
            Cliente cliente = clienteRepository
                    .findById(dto.getIdCliente())
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

            // BUSCAR EMPLEADO
            Empleado empleado = empleadoRepository
                    .findById(dto.getIdEmpleado())
                    .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));

            // BUSCAR SERVICIO
            Servicio servicio = servicioRepository
                    .findById(dto.getIdServicio())
                    .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));

            // BUSCAR DISPONIBILIDAD
            Disponibilidad bloque = disponibilidadRepository
                    .findById(dto.getIdDisponibilidad())
                    .orElseThrow(() -> new RuntimeException("Bloque no encontrado"));

            // VALIDAR SI YA ESTÁ OCUPADO
            if (bloque.getEstadoBloque().equalsIgnoreCase("ocupado")) {
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body("Horario ocupado");
            }

            // LA CITA OCUPA SOLO LA DURACIÓN DEL SERVICIO, NO EL BLOQUE COMPLETO
            if (dto.getHoraInicio() == null || dto.getHoraInicio().isBlank()) {
                return ResponseEntity
                        .badRequest()
                        .body("Debes indicar la hora de inicio de la cita");
            }

            LocalTime horaInicio = LocalTime.parse(dto.getHoraInicio());
            int duracionMin = parseDuracionMinutos(servicio.getDuracion());
            LocalTime horaFin = horaInicio.plusMinutes(duracionMin);

            LocalTime inicioBloque = bloque.getHoraInicioBloque();
            LocalTime finBloque = bloque.getHoraFinBloque();

            if (horaInicio.isBefore(inicioBloque)
                    || horaFin.isAfter(finBloque)
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
            cita.setFechaCreacion(LocalDateTime.now());

            Cita citaGuardada = citaRepository.save(cita);

            // DIVIDIR EL BLOQUE: solo el tramo de la cita queda ocupado
            if (horaInicio.equals(inicioBloque)) {
                // La cita inicia donde inicia el bloque: el bloque se convierte en el tramo ocupado
                bloque.setHoraFinBloque(horaFin);
                bloque.setEstadoBloque("ocupado");
                disponibilidadRepository.save(bloque);
            } else {
                // Tramo libre antes de la cita: el bloque original se recorta y sigue disponible
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

            // Tramo libre después de la cita: se crea un bloque disponible nuevo
            if (horaFin.isBefore(finBloque)) {
                Disponibilidad resto = new Disponibilidad();
                resto.setEmpleado(empleado);
                resto.setFecha(bloque.getFecha());
                resto.setHoraInicioBloque(horaFin);
                resto.setHoraFinBloque(finBloque);
                resto.setEstadoBloque("disponible");
                disponibilidadRepository.save(resto);
            }

            // ENVIAR CORREO
            try {
                emailService.enviarConfirmacionCita(citaGuardada);
            } catch (Exception e) {
                System.err.println("Error enviando correo: " + e.getMessage());
            }

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

    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelarCita(@PathVariable Integer id) {

        try {

            Cita cita = citaRepository.findById(id)
                    .orElse(null);

            if (cita == null) {
                return ResponseEntity.notFound().build();
            }

            if ("cancelada".equalsIgnoreCase(cita.getEstadoCita())) {
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body("La cita ya estaba cancelada.");
            }

            cita.setEstadoCita("cancelada");
            citaRepository.save(cita);

            // Restaurar el bloque de disponibilidad correspondiente
            disponibilidadRepository
                    .findByEmpleado_IdEmpleadoAndFechaAndHoraInicioBloqueAndHoraFinBloqueAndEstadoBloque(
                            cita.getEmpleado().getIdEmpleado(),
                            cita.getFechaCita(),
                            cita.getHoraInicio(),
                            cita.getHoraFin(),
                            "ocupado")
                    .ifPresent(bloque -> {
                        bloque.setEstadoBloque("disponible");
                        disponibilidadRepository.save(bloque);
                    });

            return ResponseEntity.ok("Cita cancelada exitosamente.");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
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