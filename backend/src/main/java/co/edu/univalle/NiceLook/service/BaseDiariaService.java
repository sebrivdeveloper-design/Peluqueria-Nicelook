package co.edu.univalle.NiceLook.service;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.univalle.NiceLook.model.BaseDiaria;
import co.edu.univalle.NiceLook.repository.BaseDiariaRepository;

@Service
public class BaseDiariaService {

    @Autowired
    private BaseDiariaRepository baseDiariaRepository;

    public void registrarBase(BigDecimal monto) {

        LocalDate hoy = LocalDate.now();

        if (baseDiariaRepository.existsByFecha(hoy)) {
            throw new RuntimeException(
                    "Ya existe una base registrada para hoy");
        }

        BaseDiaria base = new BaseDiaria();

        base.setFecha(hoy);
        base.setMontoInicial(monto);

        baseDiariaRepository.save(base);
    }

    public BaseDiaria obtenerBaseHoy() {

        return baseDiariaRepository
                .findByFecha(LocalDate.now())
                .orElse(null);
    }
}
