package co.edu.univalle.NiceLook.controller;

import java.time.LocalDate;
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

import co.edu.univalle.NiceLook.DTO.DisponibilidadDTO;
import co.edu.univalle.NiceLook.model.Disponibilidad;
import co.edu.univalle.NiceLook.model.Empleado;
import co.edu.univalle.NiceLook.repository.DisponibilidadRepository;
import co.edu.univalle.NiceLook.repository.EmpleadoRepository;

@RestController
@RequestMapping("/api/disponibilidad")
@CrossOrigin(origins = "*")
public class DisponibilidadController {

    @Autowired
    private DisponibilidadRepository disponibilidadRepository;

    @Autowired
    private EmpleadoRepository empleadoRepository;

    // GET por empleado y mes
    @GetMapping("/{idEmpleado}")
    public ResponseEntity<?> getDisponibilidad(
            @PathVariable Integer idEmpleado,
            @RequestParam String mes,
            @RequestParam String anio) {

        LocalDate inicio = LocalDate.of(Integer.parseInt(anio), Integer.parseInt(mes), 1);
        LocalDate fin = inicio.withDayOfMonth(inicio.lengthOfMonth());

        List<Disponibilidad> bloques = disponibilidadRepository
            .findByEmpleadoAndFechaBetween(idEmpleado, inicio, fin);

        return ResponseEntity.ok(bloques);
    }

    // POST registrar bloque de disponibilidad
    @PostMapping
    public ResponseEntity<?> registrarDisponibilidad(@RequestBody DisponibilidadDTO dto) {
        Empleado empleado = empleadoRepository.findById(dto.getIdEmpleado())
            .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));

        Disponibilidad disponibilidad = new Disponibilidad();
        disponibilidad.setEmpleado(empleado);
        disponibilidad.setFecha(LocalDate.parse(dto.getFecha()));
        disponibilidad.setHoraInicioBloque(java.time.LocalTime.parse(dto.getHoraInicioBloque()));
        disponibilidad.setHoraFinBloque(java.time.LocalTime.parse(dto.getHoraFinBloque()));
        disponibilidad.setEstadoBloque(dto.getEstadoBloque());

        disponibilidadRepository.save(disponibilidad);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body("Horario registrado exitosamente.");
    }
}