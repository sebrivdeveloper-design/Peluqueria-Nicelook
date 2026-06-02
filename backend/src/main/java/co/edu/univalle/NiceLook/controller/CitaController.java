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
public ResponseEntity<?> registrarCita(
        @RequestBody RegistroCitaDTO dto
) {

    try {

        System.out.println("DTO: " + dto.getIdDisponibilidad());

    // BUSCAR CLIENTE
    Cliente cliente = clienteRepository
        .findById(dto.getIdCliente())
        .orElseThrow(() ->
            new RuntimeException("Cliente no encontrado")
        );

    // BUSCAR EMPLEADO
    Empleado empleado = empleadoRepository
        .findById(dto.getIdEmpleado())
        .orElseThrow(() ->
            new RuntimeException("Empleado no encontrado")
        );

    // BUSCAR SERVICIO
    Servicio servicio = servicioRepository
        .findById(dto.getIdServicio())
        .orElseThrow(() ->
            new RuntimeException("Servicio no encontrado")
        );
        
System.out.println("DTO RECIBIDO");
System.out.println(dto.getIdCliente());
System.out.println(dto.getIdEmpleado());
System.out.println(dto.getIdServicio());
System.out.println(dto.getIdDisponibilidad());

    // BUSCAR DISPONIBILIDAD
    Disponibilidad bloque =
        disponibilidadRepository
            .findById(dto.getIdDisponibilidad())
            .orElseThrow(() ->
                new RuntimeException(
                    "Bloque no encontrado"
                )
            );

    // VALIDAR SI YA ESTÁ OCUPADO
    if (
        bloque.getEstadoBloque()
            .equalsIgnoreCase("ocupado")
    ) {

        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body("Horario ocupado");
    }
    // CREAR CITA
    Cita cita = new Cita();

    cita.setCliente(cliente);

    cita.setEmpleado(empleado);

    cita.setServicio(servicio);

    cita.setFechaCita(
        bloque.getFecha()
    );

    cita.setHoraInicio(
        bloque.getHoraInicioBloque()
    );

    cita.setHoraFin(
        bloque.getHoraFinBloque()
    );

    cita.setEstadoCita("pendiente");

    cita.setFechaCreacion(
        LocalDateTime.now()
    );

    // GUARDAR CITA
    Cita citaGuardada =
        citaRepository.save(cita);

    // MARCAR BLOQUE COMO OCUPADO
    bloque.setEstadoBloque("ocupado");

    disponibilidadRepository.save(bloque);

    // ENVIAR CORREO
    try {

        emailService
            .enviarConfirmacionCita(
                citaGuardada
            );

    } catch (Exception e) {

        System.err.println(
            "Error enviando correo: "
            + e.getMessage()
        );
    }

    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body("Cita registrada exitosamente.");
}

   catch (Exception e) {

        e.printStackTrace();

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(e.getMessage());
    }
}
}