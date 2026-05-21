package co.edu.univalle.NiceLook.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import co.edu.univalle.NiceLook.model.Cita;
import co.edu.univalle.NiceLook.repository.CitaRepository;

@Component
@EnableScheduling
public class RecordatorioScheduler {

    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private EmailService emailService;

    // Corre todos los días a las 9:00 AM
    @Scheduled(cron = "0 0 9 * * *")
    public void enviarRecordatorios() {
        LocalDate manana = LocalDate.now().plusDays(1);
        List<Cita> citas = citaRepository.findCitasPorFecha(manana);
        for (Cita cita : citas) {
            try {
                emailService.enviarRecordatorio(cita);
            } catch (Exception e) {
                System.err.println("Error enviando recordatorio para cita " + 
                    cita.getIdCita() + ": " + e.getMessage());
            }
        }
    }
}