package co.edu.univalle.NiceLook.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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

    // GET horarios disponibles de un empleado en una fecha
    @GetMapping("/disponibilidad/{idEmpleado}")
    public ResponseEntity<?> getDisponibilidad(
            @PathVariable Integer idEmpleado,
            @RequestParam String fecha) {
        LocalDate localFecha = LocalDate.parse(fecha);
        List<Disponibilidad> bloques = disponibilidadRepository
            .findByEmpleado_IdEmpleadoAndFechaAndEstadoBloque(idEmpleado, localFecha, "disponible");
        return ResponseEntity.ok(bloques);
    }

    @GetMapping("/horarios-disponibles")
    public ResponseEntity<?> getHorariosDisponibles(
            @RequestParam Integer idEmpleado,
            @RequestParam String fecha,
            @RequestParam Integer idServicio) {
        try {
            Servicio servicio = servicioRepository
                    .findById(idServicio)
                    .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));

            Integer duracionMinutos = servicio.getDuracionMinutos();
            int bloquesNecesarios = duracionMinutos / 30;
            LocalDate localFecha = LocalDate.parse(fecha);

            List<Disponibilidad> bloques = disponibilidadRepository
                    .findByEmpleado_IdEmpleadoAndFechaAndEstadoBloque(idEmpleado, localFecha, "disponible");

            List<Disponibilidad> horariosValidos = new java.util.ArrayList<>();

            for (int i = 0; i < bloques.size(); i++) {
                boolean disponible = true;
                for (int j = 0; j < bloquesNecesarios; j++) {
                    if (i + j >= bloques.size()) {
                        disponible = false;
                        break;
                    }
                    Disponibilidad actual = bloques.get(i + j);
                    if (j > 0) {
                        Disponibilidad anterior = bloques.get(i + j - 1);
                        if (!anterior.getHoraFinBloque().equals(actual.getHoraInicioBloque())) {
                            disponible = false;
                            break;
                        }
                    }
                }
                if (disponible) {
                    horariosValidos.add(bloques.get(i));
                }
            }
            return ResponseEntity.ok(horariosValidos);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // POST registrar cita
    @PostMapping
    public ResponseEntity<?> registrarCita(@RequestBody RegistroCitaDTO dto) {
        try {
            // BUSCAR ENTIDADES
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

            // VALIDAR SI YA ESTÁ OCUPADO
            if (bloque.getEstadoBloque().equalsIgnoreCase("ocupado")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Horario ocupado");
            }

            // CALCULAR HORARIOS
            LocalTime horaInicio = bloque.getHoraInicioBloque();
            LocalTime horaFin = horaInicio.plusMinutes(servicio.getDuracionMinutos());

            if (horaFin.isAfter(bloque.getHoraFinBloque())) {
                return ResponseEntity.badRequest().body("El servicio excede el tiempo disponible.");
            }

            // CREAR Y GUARDAR CITA
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

            // ✅ FIX: Siempre marca el bloque original como ocupado
            // con la hora de inicio ORIGINAL para que el mapeo en
            // DisponibilidadController encuentre la cita correctamente.
            bloque.setEstadoBloque("ocupado");
            disponibilidadRepository.save(bloque);

            // ✅ FIX: Solo crear bloque restante si sobran >= 30 minutos completos.
            // Sobras menores de 30 min no sirven para ningún servicio y generan
            // bloques diminutos que se ven cortados en el calendario.
            long minutosRestantes = java.time.Duration
                    .between(horaFin, bloque.getHoraFinBloque())
                    .toMinutes();

            if (minutosRestantes >= 30) {
                Disponibilidad bloqueRestante = new Disponibilidad();
                bloqueRestante.setEmpleado(bloque.getEmpleado());
                bloqueRestante.setFecha(bloque.getFecha());
                bloqueRestante.setHoraInicioBloque(horaFin);
                bloqueRestante.setHoraFinBloque(bloque.getHoraFinBloque());
                bloqueRestante.setEstadoBloque("disponible");
                disponibilidadRepository.save(bloqueRestante);
            }

            // ENVIAR CORREO
            try {
                emailService.enviarConfirmacionCita(citaGuardada);
            } catch (Exception e) {
                System.err.println("Error enviando correo: " + e.getMessage());
            }

            return ResponseEntity.status(HttpStatus.CREATED).body("Cita registrada exitosamente.");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}