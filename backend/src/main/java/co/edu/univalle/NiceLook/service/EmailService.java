package co.edu.univalle.NiceLook.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Async;
import co.edu.univalle.NiceLook.model.Cita;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // El remitente debe coincidir con la cuenta autenticada (spring.mail.username),
    // de lo contrario el proveedor SMTP rechaza el envío. Se puede sobreescribir con
    // app.mail.from si se usa un remitente verificado distinto.
    @Value("${app.mail.from:${spring.mail.username}}")
    private String fromEmail;

    // Código OTP para verificar reservas desde la landing pública (CHANGE 4 - V2)
    public void enviarOtp(String correo, String nombre, String codigo) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(fromEmail);
        msg.setTo(correo);
        msg.setSubject("Tu código de verificación NiceLook: " + codigo);
        msg.setText(
            "Hola " + (nombre != null && !nombre.isBlank() ? nombre : "") + ",\n\n" +
            "Tu código de verificación para confirmar la reserva en NiceLook es:\n\n" +
            "        " + codigo + "\n\n" +
            "El código vence en 10 minutos. Si no solicitaste esta reserva, ignora este mensaje."
        );
        mailSender.send(msg);
    }

    @Async
    public void enviarConfirmacionCita(Cita cita) {
        String correoCliente = cita.getCliente().getUsuario().getCorreo();
        String correoEmpleado = cita.getEmpleado().getUsuario().getCorreo();
        String nombreCliente = cita.getCliente().getUsuario().getNombreCompleto();
        String nombreBarbero = cita.getEmpleado().getUsuario().getNombreCompleto();
        String fecha = cita.getFechaCita().toString();
        String hora = cita.getHoraInicio().toString();

        // Correo al cliente
        SimpleMailMessage msgCliente = new SimpleMailMessage();
        msgCliente.setFrom(fromEmail);
        msgCliente.setTo(correoCliente);
        msgCliente.setSubject("✅ Confirmación de tu cita en NiceLook");
        msgCliente.setText(
            "Hola " + nombreCliente + ",\n\n" +
            "Tu cita ha sido registrada exitosamente.\n\n" +
            "📅 Fecha: " + fecha + "\n" +
            "⏰ Hora: " + hora + "\n" +
            "💈 Barbero: " + nombreBarbero + "\n\n" +
            "Te esperamos en NiceLook.\n" +
            "Si necesitas cancelar, contáctanos con anticipación."
        );
        mailSender.send(msgCliente);

        // Correo al barbero
        SimpleMailMessage msgBarbero = new SimpleMailMessage();
        msgBarbero.setFrom(fromEmail);
        msgBarbero.setTo(correoEmpleado);
        msgBarbero.setSubject("📋 Nueva cita asignada - NiceLook");
        msgBarbero.setText(
            "Hola " + nombreBarbero + ",\n\n" +
            "Tienes una nueva cita asignada.\n\n" +
            "👤 Cliente: " + nombreCliente + "\n" +
            "📅 Fecha: " + fecha + "\n" +
            "⏰ Hora: " + hora + "\n\n" +
            "Recuerda estar disponible a tiempo."
        );
        mailSender.send(msgBarbero);
    }

    public void enviarRecordatorio(Cita cita) {
        String correoCliente = cita.getCliente().getUsuario().getCorreo();
        String correoEmpleado = cita.getEmpleado().getUsuario().getCorreo();
        String nombreCliente = cita.getCliente().getUsuario().getNombreCompleto();
        String nombreBarbero = cita.getEmpleado().getUsuario().getNombreCompleto();
        String fecha = cita.getFechaCita().toString();
        String hora = cita.getHoraInicio().toString();

        // Recordatorio al cliente
        SimpleMailMessage msgCliente = new SimpleMailMessage();
        msgCliente.setFrom(fromEmail);
        msgCliente.setTo(correoCliente);
        msgCliente.setSubject("⏰ Recordatorio: Tu cita en NiceLook es mañana");
        msgCliente.setText(
            "Hola " + nombreCliente + ",\n\n" +
            "Te recordamos que mañana tienes una cita en NiceLook.\n\n" +
            "📅 Fecha: " + fecha + "\n" +
            "⏰ Hora: " + hora + "\n" +
            "💈 Barbero: " + nombreBarbero + "\n\n" +
            "¡Te esperamos!"
        );
        mailSender.send(msgCliente);

        // Recordatorio al barbero
        SimpleMailMessage msgBarbero = new SimpleMailMessage();
        msgBarbero.setFrom(fromEmail);
        msgBarbero.setTo(correoEmpleado);
        msgBarbero.setSubject("⏰ Recordatorio: Cita mañana - NiceLook");
        msgBarbero.setText(
            "Hola " + nombreBarbero + ",\n\n" +
            "Recuerda que mañana tienes una cita.\n\n" +
            "👤 Cliente: " + nombreCliente + "\n" +
            "📅 Fecha: " + fecha + "\n" +
            "⏰ Hora: " + hora + "\n\n" +
            "¡Hasta mañana!"
        );
        mailSender.send(msgBarbero);
    }
}