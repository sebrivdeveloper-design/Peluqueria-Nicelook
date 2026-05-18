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
import co.edu.univalle.NiceLook.service.EmailService;

@RestController
@RequestMapping("/api/citas")
@CrossOrigin(origins = "*")
public class CitaController {

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

        // Verifica si el horario ya está ocupado
        boolean ocupado = citaRepository.existeCitaEnHorario(
            dto.getIdEmpleado(),
            LocalDate.parse(dto.getFecha()),
            LocalTime.parse(dto.getHoraInicio())
        );

        if (ocupado) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("El horario seleccionado ya fue reservado. Por favor elige otro.");
        }

        // Buscar cliente y empleado
        Cliente cliente = clienteRepository.findById(dto.getIdCliente())
            .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        Empleado empleado = empleadoRepository.findById(dto.getIdEmpleado())
            .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));

        // Crear la cita
        Cita cita = new Cita();
        cita.setCliente(cliente);
        cita.setEmpleado(empleado);
        cita.setFechaCita(LocalDate.parse(dto.getFecha()));
        cita.setHoraInicio(LocalTime.parse(dto.getHoraInicio()));
        cita.setHoraFin(LocalTime.parse(dto.getHoraFin()));
        cita.setEstadoCita("pendiente");
        cita.setObservaciones(dto.getObservaciones());
        cita.setFechaCreacion(LocalDateTime.now());
        Cita citaGuardada = citaRepository.save(cita);

        // Marcar bloque como ocupado
        List<Disponibilidad> bloques = disponibilidadRepository
            .findByEmpleado_IdEmpleadoAndFechaAndEstadoBloque(
                dto.getIdEmpleado(),
                LocalDate.parse(dto.getFecha()),
                "disponible"
            );
        bloques.stream()
            .filter(b -> b.getHoraInicioBloque().equals(LocalTime.parse(dto.getHoraInicio())))
            .findFirst()
            .ifPresent(b -> {
                b.setEstadoBloque("ocupado");
                disponibilidadRepository.save(b);
            });

        // Enviar correos
        try {
            emailService.enviarConfirmacionCita(citaGuardada);
        } catch (Exception e) {
            System.err.println("Error enviando correo: " + e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.CREATED)
            .body("Cita registrada exitosamente.");
    }
}