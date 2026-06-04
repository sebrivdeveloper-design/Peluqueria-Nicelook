package co.edu.univalle.NiceLook.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.univalle.NiceLook.DTO.HistorialCitaDTO;
import co.edu.univalle.NiceLook.model.Cita;
import co.edu.univalle.NiceLook.repository.CitaRepository;

@Service
public class HistorialService {

    @Autowired
    private CitaRepository citaRepository;

    public List<HistorialCitaDTO> obtenerHistorial(Integer idCliente) {

        List<Cita> citas = citaRepository.findByCliente_IdCliente(idCliente);

        List<HistorialCitaDTO> historial = new ArrayList<>();

        for (Cita cita : citas) {

            HistorialCitaDTO dto = new HistorialCitaDTO(
                    cita.getIdCita(),
                    cita.getFechaCita().toString(),
                    cita.getHoraInicio().toString(),
                    cita.getHoraFin().toString(),
                    cita.getServicio().getNombreServicio(),
                    cita.getEmpleado().getUsuario().getNombreCompleto(),
                    cita.getEstadoCita()
            );

            historial.add(dto);
        }

        return historial;
    }
}