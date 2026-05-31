package co.edu.univalle.NiceLook.service;

import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

/**
 * Lógica única para crear una cita, respetando disponibilidad y bloqueo de horarios.
 * La usan tanto el panel interno (CitaController) como la landing pública
 * (PublicReservaController), evitando duplicar la lógica de reserva.
 */
@Service
public class ReservaService {

    @Autowired private ServicioRepository servicioRepository;
    @Autowired private CitaRepository citaRepository;
    @Autowired private ClienteRepository clienteRepository;
    @Autowired private EmpleadoRepository empleadoRepository;
    @Autowired private DisponibilidadRepository disponibilidadRepository;
    @Autowired private EmailService emailService;
    @Autowired private NotificacionService notificacionService;

    /**
     * Crea una cita validando la disponibilidad y dividiendo el bloque. Envía el correo
     * de confirmación y notifica al staff (HU-42). Lanza RuntimeException con un mensaje
     * claro ante cualquier error de negocio.
     *
     * @param origen texto para la notificación al staff (p. ej. "landing" o "panel").
     */
    @Transactional
    public Cita crearCita(RegistroCitaDTO dto, String origen) {

        Cliente cliente = clienteRepository.findById(dto.getIdCliente())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        Empleado empleado = empleadoRepository.findById(dto.getIdEmpleado())
                .orElseThrow(() -> new RuntimeException("Estilista no encontrado"));

        Servicio servicio = servicioRepository.findById(dto.getIdServicio())
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));

        Disponibilidad bloque = disponibilidadRepository.findById(dto.getIdDisponibilidad())
                .orElseThrow(() -> new RuntimeException("Bloque no encontrado"));

        if (!"disponible".equalsIgnoreCase(bloque.getEstadoBloque())) {
            throw new RuntimeException("Horario no disponible");
        }

        if (dto.getHoraInicio() == null || dto.getHoraInicio().isBlank()) {
            throw new RuntimeException("Debes indicar la hora de inicio de la cita");
        }

        LocalTime horaInicio = LocalTime.parse(dto.getHoraInicio());
        int duracionMin = servicio.getDuracionMinutos();
        LocalTime horaFin = horaInicio.plusMinutes(duracionMin);

        if (horaInicio.isBefore(bloque.getHoraInicioBloque())
                || horaFin.isAfter(bloque.getHoraFinBloque())
                || !horaFin.isAfter(horaInicio)) {
            throw new RuntimeException("El servicio (" + duracionMin
                    + " min) no cabe en el horario seleccionado");
        }

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

        try {
            emailService.enviarConfirmacionCita(citaGuardada);
        } catch (Exception e) {
            System.err.println("Error enviando correo: " + e.getMessage());
        }

        notificacionService.notificarStaff(
                "info",
                "Nueva cita registrada" + (origen != null ? " (" + origen + ")" : ""),
                cliente.getUsuario().getNombreCompleto() + " · "
                        + servicio.getNombreServicio() + " · "
                        + bloque.getFecha() + " " + horaInicio);

        return citaGuardada;
    }

    /**
     * Divide un bloque disponible: el tramo [horaInicio, horaFin] queda ocupado y los
     * tramos sobrantes permanecen disponibles. (Misma lógica usada por la agenda interna.)
     */
    public void dividirBloque(Disponibilidad bloque, LocalTime horaInicio, LocalTime horaFin) {

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

    // La duración del servicio se guarda como texto libre ("30 min", "1 hora").
    public int parseDuracionMinutos(String duracion) {
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
