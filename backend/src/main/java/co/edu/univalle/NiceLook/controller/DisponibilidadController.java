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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.univalle.NiceLook.DTO.AgendaEmpleadoDTO;
import co.edu.univalle.NiceLook.DTO.DisponibilidadDTO;
import co.edu.univalle.NiceLook.model.Disponibilidad;
import co.edu.univalle.NiceLook.model.Empleado;
import co.edu.univalle.NiceLook.repository.DisponibilidadRepository;
import co.edu.univalle.NiceLook.repository.EmpleadoRepository;
import java.time.LocalTime;
import co.edu.univalle.NiceLook.DTO.AgendaCitaDTO;
import co.edu.univalle.NiceLook.repository.CitaRepository;

@RestController
@RequestMapping("/api/disponibilidad")
@CrossOrigin(origins = "*")
public class DisponibilidadController {

    @Autowired
    private DisponibilidadRepository disponibilidadRepository;

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private CitaRepository citaRepository;

    // GET por empleado y mes
    @GetMapping("/{idEmpleado}")
    public ResponseEntity<?> getDisponibilidad(
            @PathVariable Integer idEmpleado,
            @RequestParam(required = false) Integer mes,
            @RequestParam(required = false) Integer anio) {

        if (mes == null || anio == null) {
            LocalDate hoy = LocalDate.now();
            mes = hoy.getMonthValue();
            anio = hoy.getYear();
        }

        LocalDate inicio = LocalDate.of(anio, mes, 1);
        LocalDate fin = inicio.withDayOfMonth(inicio.lengthOfMonth());

        List<Disponibilidad> bloques = disponibilidadRepository
                .findByEmpleadoAndFechaBetween(idEmpleado, inicio, fin);

        List<AgendaCitaDTO> citas = citaRepository.findCitasDelMes(idEmpleado, inicio, fin);

        List<AgendaEmpleadoDTO> response = bloques.stream().map(bloque -> {

            AgendaEmpleadoDTO dto = new AgendaEmpleadoDTO();
            dto.setIdDisponibilidad(bloque.getIdDisponibilidad());
            dto.setFecha(bloque.getFecha().toString());
            dto.setHoraInicio(bloque.getHoraInicioBloque().toString());
            dto.setHoraFin(bloque.getHoraFinBloque().toString());
            dto.setEstado(bloque.getEstadoBloque());

            // ✅ FIX: Si está ocupado, busca la cita cuya horaInicio caiga
            // DENTRO del rango del bloque (en vez de comparar strings exactos).
            // Esto evita fallos por formato de hora o diferencias de segundos.
            if (bloque.getEstadoBloque().equalsIgnoreCase("ocupado")) {

                AgendaCitaDTO citaEncontrada = citas.stream()
                        .filter(c ->
                            c.getFechaCita().equals(bloque.getFecha()) &&
                            !c.getHoraInicio().isBefore(bloque.getHoraInicioBloque()) &&
                            c.getHoraInicio().isBefore(bloque.getHoraFinBloque())
                        )
                        .findFirst()
                        .orElse(null);

                if (citaEncontrada != null) {
                    dto.setCliente(citaEncontrada.getCliente());
                    dto.setServicio(citaEncontrada.getServicio());
                }
            }

            return dto;
        }).toList();

        return ResponseEntity.ok(response);
    }

    // POST registrar bloque de disponibilidad
    @PostMapping
    public ResponseEntity<?> registrarDisponibilidad(
            @RequestBody DisponibilidadDTO dto) {

        Empleado empleado = empleadoRepository.findById(dto.getIdEmpleado())
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));

        boolean existeCruce = disponibilidadRepository
                .existsByEmpleado_IdEmpleadoAndFechaAndHoraInicioBloqueLessThanAndHoraFinBloqueGreaterThan(
                        dto.getIdEmpleado(),
                        LocalDate.parse(dto.getFecha()),
                        LocalTime.parse(dto.getHoraFinBloque()),
                        LocalTime.parse(dto.getHoraInicioBloque()));

        if (existeCruce) {
            return ResponseEntity.badRequest().body("Ya existe un horario en ese rango");
        }

        LocalTime inicio = LocalTime.parse(dto.getHoraInicioBloque());
        LocalTime fin = LocalTime.parse(dto.getHoraFinBloque());

        while (inicio.isBefore(fin)) {
            LocalTime siguiente = inicio.plusMinutes(30);

            Disponibilidad bloque = new Disponibilidad();
            bloque.setEmpleado(empleado);
            bloque.setFecha(LocalDate.parse(dto.getFecha()));
            bloque.setHoraInicioBloque(inicio);
            bloque.setHoraFinBloque(siguiente);
            bloque.setEstadoBloque("disponible");

            disponibilidadRepository.save(bloque);
            inicio = siguiente;
        }

        return ResponseEntity.status(HttpStatus.CREATED).body("Horario registrado exitosamente.");
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editarDisponibilidad(
            @PathVariable Integer id,
            @RequestBody DisponibilidadDTO dto) {

        try {
            Disponibilidad bloque = disponibilidadRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Bloque no encontrado"));

            boolean tieneCitas = citaRepository.existeCitaEnBloque(
                    bloque.getEmpleado().getIdEmpleado(),
                    bloque.getFecha(),
                    bloque.getHoraInicioBloque(),
                    bloque.getHoraFinBloque());

            if (tieneCitas) {
                return ResponseEntity.badRequest()
                        .body("Hay clientes agendados, comuníquese con recepción.");
            }

            boolean existeCruce = disponibilidadRepository
                    .existsByEmpleado_IdEmpleadoAndFechaAndHoraInicioBloqueLessThanAndHoraFinBloqueGreaterThan(
                            dto.getIdEmpleado(),
                            LocalDate.parse(dto.getFecha()),
                            LocalTime.parse(dto.getHoraFinBloque()),
                            LocalTime.parse(dto.getHoraInicioBloque()));

            boolean mismoHorario =
                    bloque.getFecha().equals(LocalDate.parse(dto.getFecha())) &&
                    bloque.getHoraInicioBloque().equals(LocalTime.parse(dto.getHoraInicioBloque())) &&
                    bloque.getHoraFinBloque().equals(LocalTime.parse(dto.getHoraFinBloque()));

            if (existeCruce && !mismoHorario) {
                return ResponseEntity.badRequest().body("Ya existe un horario en ese rango");
            }

            bloque.setFecha(LocalDate.parse(dto.getFecha()));
            bloque.setHoraInicioBloque(LocalTime.parse(dto.getHoraInicioBloque()));
            bloque.setHoraFinBloque(LocalTime.parse(dto.getHoraFinBloque()));
            disponibilidadRepository.save(bloque);

            return ResponseEntity.ok("Horario actualizado correctamente");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}