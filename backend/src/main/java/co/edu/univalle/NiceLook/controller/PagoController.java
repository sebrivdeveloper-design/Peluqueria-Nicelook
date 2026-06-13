package co.edu.univalle.NiceLook.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import co.edu.univalle.NiceLook.model.Cita;
import co.edu.univalle.NiceLook.model.Pago;
import co.edu.univalle.NiceLook.repository.CitaRepository;
import co.edu.univalle.NiceLook.repository.PagoRepository;

@RestController
@RequestMapping("/api/pagos")
@CrossOrigin(origins = "*")
public class PagoController {

    private static final List<String> MEDIOS_VALIDOS =
            List.of("efectivo", "transferencia", "tarjeta");

    @Autowired private PagoRepository pagoRepository;
    @Autowired private CitaRepository citaRepository;

    // GET citas finalizadas de una fecha que aún no tienen pago (para cobrar)
    @GetMapping("/pendientes")
    public ResponseEntity<?> citasPorCobrar(@RequestParam(required = false) String fecha) {

        LocalDate dia = (fecha == null || fecha.isBlank())
                ? LocalDate.now()
                : LocalDate.parse(fecha);

        List<Cita> finalizadas = citaRepository.findFinalizadasPorFecha(dia);

        List<Map<String, Object>> response = finalizadas.stream()
                .filter(c -> !pagoRepository.existsByCita_IdCitaAndEstadoPago(c.getIdCita(), "completado"))
                .map(c -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("idCita", c.getIdCita());
                    m.put("horaInicio", c.getHoraInicio().toString());
                    m.put("cliente", c.getCliente().getUsuario() != null
                            ? c.getCliente().getUsuario().getNombreCompleto() : "");
                    m.put("servicio", c.getServicio().getNombreServicio());
                    m.put("empleado", c.getEmpleado().getUsuario() != null
                            ? c.getEmpleado().getUsuario().getNombreCompleto() : "");
                    m.put("valor", c.getServicio().getPrecio());
                    return m;
                }).toList();

        return ResponseEntity.ok(response);
    }

    // POST registrar pago de una cita finalizada
    @PostMapping
    public ResponseEntity<?> registrarPago(@RequestBody Map<String, String> body) {

        try {

            Integer idCita;
            try {
                idCita = Integer.valueOf(body.get("idCita"));
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("Cita inválida.");
            }

            String medio = body.getOrDefault("metodoPago", "").toLowerCase().trim();

            if (!MEDIOS_VALIDOS.contains(medio)) {
                return ResponseEntity
                        .badRequest()
                        .body("Medio de pago inválido. Usa: efectivo, transferencia o tarjeta.");
            }

            Cita cita = citaRepository.findById(idCita).orElse(null);
            if (cita == null) return ResponseEntity.notFound().build();

            if (!"finalizada".equalsIgnoreCase(cita.getEstadoCita())) {
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body("Solo se pueden cobrar citas finalizadas.");
            }

            if (pagoRepository.existsByCita_IdCitaAndEstadoPago(idCita, "completado")) {
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body("Esta cita ya tiene un pago registrado.");
            }

            Pago pago = new Pago();
            pago.setCita(cita);
            pago.setMontoTotal(cita.getServicio().getPrecio().doubleValue());
            pago.setMetodoPago(medio);
            pago.setReferencia("PAG-" + System.currentTimeMillis());
            pago.setEstadoPago("completado");
            pago.setFechaPago(LocalDateTime.now());

            pagoRepository.save(pago);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body("Pago registrado exitosamente.");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    // GET pagos del día (historial para cierre de caja)
    @GetMapping("/dia")
    public ResponseEntity<?> pagosDelDia(@RequestParam(required = false) String fecha) {

        LocalDate dia = (fecha == null || fecha.isBlank())
                ? LocalDate.now()
                : LocalDate.parse(fecha);

        List<Pago> pagos = pagoRepository.findByFechaPagoBetweenOrderByFechaPagoDesc(
                dia.atStartOfDay(),
                dia.atTime(23, 59, 59));

        List<Map<String, Object>> response = pagos.stream().map(p -> {
            Map<String, Object> m = new HashMap<>();
            m.put("idPago", p.getIdPago());
            m.put("hora", p.getFechaPago().toLocalTime().toString());
            m.put("cliente", p.getCita().getCliente().getUsuario() != null
                    ? p.getCita().getCliente().getUsuario().getNombreCompleto() : "");
            m.put("empleado", p.getCita().getEmpleado().getUsuario() != null
                    ? p.getCita().getEmpleado().getUsuario().getNombreCompleto() : "");
            m.put("servicio", p.getCita().getServicio().getNombreServicio());
            m.put("metodoPago", p.getMetodoPago());
            m.put("valor", p.getMontoTotal());
            m.put("estadoPago", p.getEstadoPago());
            m.put("referencia", p.getReferencia());
            return m;
        }).toList();

        return ResponseEntity.ok(response);
    }

    // PUT anular pago
    @PutMapping("/{id}/anular")
    public ResponseEntity<?> anularPago(@PathVariable Integer id) {

        Pago pago = pagoRepository.findById(id).orElse(null);

        if (pago == null) return ResponseEntity.notFound().build();

        if ("anulado".equalsIgnoreCase(pago.getEstadoPago())) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("El pago ya se encuentra anulado.");
        }

        // El registro se conserva para auditoría; solo cambia el estado
        pago.setEstadoPago("anulado");
        pagoRepository.save(pago);

        return ResponseEntity.ok("Pago anulado. Quedó excluido del cierre de caja.");
    }
}
