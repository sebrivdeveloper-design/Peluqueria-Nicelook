package co.edu.univalle.NiceLook.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Verificación por código de un solo uso (OTP) para la landing pública de reservas.
 *
 * MVP: el código se envía por correo (reutiliza {@link EmailService}) y se guarda en
 * memoria con vencimiento. No persiste en BD a propósito: los OTP son efímeros (10 min)
 * y un reinicio del servidor simplemente invalida los pendientes, lo cual es seguro.
 * Ver CHANGES_V2.md para cómo evolucionar a SMS o almacenamiento en BD.
 */
@Service
public class OtpService {

    private static final int VIGENCIA_MINUTOS = 10;
    private static final int MAX_INTENTOS = 5;
    private static final SecureRandom RANDOM = new SecureRandom();

    @Autowired private EmailService emailService;

    private record Otp(String codigo, LocalDateTime expira, int intentos) { }

    // Clave: correo en minúsculas
    private final ConcurrentHashMap<String, Otp> codigos = new ConcurrentHashMap<>();

    /** Genera y envía un código al correo. Reemplaza cualquier código anterior. */
    public void solicitar(String correo, String nombre) {
        if (correo == null || correo.isBlank()) {
            throw new RuntimeException("Debes indicar un correo válido.");
        }
        String key = correo.trim().toLowerCase();
        String codigo = String.format("%06d", RANDOM.nextInt(1_000_000));
        codigos.put(key, new Otp(codigo, LocalDateTime.now().plusMinutes(VIGENCIA_MINUTOS), 0));
        emailService.enviarOtp(correo.trim(), nombre, codigo);
    }

    /**
     * Verifica el código. Devuelve true si es válido (y lo consume). Lanza
     * RuntimeException con un mensaje claro si expiró, no existe o se agotaron los intentos.
     */
    public boolean verificar(String correo, String codigo) {
        if (correo == null || codigo == null) return false;
        String key = correo.trim().toLowerCase();
        Otp otp = codigos.get(key);

        if (otp == null) {
            throw new RuntimeException("No hay un código pendiente. Solicita uno nuevo.");
        }
        if (LocalDateTime.now().isAfter(otp.expira())) {
            codigos.remove(key);
            throw new RuntimeException("El código expiró. Solicita uno nuevo.");
        }
        if (otp.intentos() >= MAX_INTENTOS) {
            codigos.remove(key);
            throw new RuntimeException("Demasiados intentos. Solicita un código nuevo.");
        }
        if (!otp.codigo().equals(codigo.trim())) {
            codigos.put(key, new Otp(otp.codigo(), otp.expira(), otp.intentos() + 1));
            throw new RuntimeException("Código incorrecto.");
        }

        codigos.remove(key); // un solo uso
        return true;
    }
}
