package co.edu.univalle.NiceLook.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

import co.edu.univalle.NiceLook.model.Cita;
import co.edu.univalle.NiceLook.model.DetallePagoEmpleado;
import co.edu.univalle.NiceLook.model.Empleado;
import co.edu.univalle.NiceLook.model.PagoEmpleado;
import co.edu.univalle.NiceLook.model.Servicio;
import co.edu.univalle.NiceLook.repository.CitaRepository;
import co.edu.univalle.NiceLook.repository.EmpleadoRepository;
import co.edu.univalle.NiceLook.repository.PagoEmpleadoRepository;
import co.edu.univalle.NiceLook.repository.ServicioRepository;

@RestController
@RequestMapping("/api/pagos-empleados")
@CrossOrigin(origins = "*")
public class PagoEmpleadoController {

    @Autowired private CitaRepository citaRepository;
    @Autowired private EmpleadoRepository empleadoRepository;
    @Autowired private ServicioRepository servicioRepository;
    @Autowired private PagoEmpleadoRepository pagoEmpleadoRepository;

    // GET servicios trabajados (citas finalizadas) agrupados por servicio
    @GetMapping("/servicios-trabajados/{idEmpleado}")
    public ResponseEntity<?> serviciosTrabajados(
            @PathVariable Integer idEmpleado,
            @RequestParam String desde,
            @RequestParam String hasta) {

        List<Cita> citas = citaRepository.findFinalizadasPorEmpleadoEntre(
                idEmpleado,
                LocalDate.parse(desde),
                LocalDate.parse(hasta));

        // Agrupar por servicio
        Map<Integer, Map<String, Object>> agrupado = new LinkedHashMap<>();

        for (Cita c : citas) {
            Servicio s = c.getServicio();
            agrupado.computeIfAbsent(s.getIdServicio(), k -> {
                Map<String, Object> m = new HashMap<>();
                m.put("idServicio", s.getIdServicio());
                m.put("nombre", s.getNombreServicio());
                m.put("precioUnitario", s.getPrecio());
                m.put("porcentaje", s.getPorcentajeEmpleado() != null ? s.getPorcentajeEmpleado() : 50);
                m.put("cantidad", 0);
                return m;
            });
            Map<String, Object> m = agrupado.get(s.getIdServicio());
            m.put("cantidad", (Integer) m.get("cantidad") + 1);
        }

        // Calcular subtotal por servicio (precio * cantidad * % / 100)
        List<Map<String, Object>> resultado = new ArrayList<>();
        for (Map<String, Object> m : agrupado.values()) {
            BigDecimal precio = (BigDecimal) m.get("precioUnitario");
            int cantidad = (Integer) m.get("cantidad");
            int pct = (Integer) m.get("porcentaje");
            BigDecimal subtotal = precio
                    .multiply(BigDecimal.valueOf(cantidad))
                    .multiply(BigDecimal.valueOf(pct))
                    .divide(BigDecimal.valueOf(100));
            m.put("subtotal", subtotal);
            resultado.add(m);
        }

        return ResponseEntity.ok(resultado);
    }

    // POST registrar pago a empleado
    @PostMapping
    public ResponseEntity<?> registrarPago(@RequestBody Map<String, Object> body) {

        try {

            Integer idEmpleado = (Integer) body.get("idEmpleado");
            String desde = (String) body.get("periodoInicio");
            String hasta = (String) body.get("periodoFin");

            Empleado empleado = empleadoRepository.findById(idEmpleado)
                    .orElse(null);
            if (empleado == null) {
                return ResponseEntity.badRequest().body("Empleado no encontrado.");
            }

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> detalles =
                    (List<Map<String, Object>>) body.get("detalles");

            if (detalles == null || detalles.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body("Debes incluir al menos un servicio en el pago.");
            }

            PagoEmpleado pago = new PagoEmpleado();
            pago.setEmpleado(empleado);
            pago.setFechaPago(LocalDateTime.now());
            pago.setPeriodoInicio(LocalDate.parse(desde));
            pago.setPeriodoFin(LocalDate.parse(hasta));

            List<DetallePagoEmpleado> listaDetalles = new ArrayList<>();
            BigDecimal total = BigDecimal.ZERO;

            for (Map<String, Object> d : detalles) {
                Integer idServicio = (Integer) d.get("idServicio");
                Integer cantidad = (Integer) d.get("cantidad");

                if (cantidad == null || cantidad <= 0) continue;

                Servicio servicio = servicioRepository.findById(idServicio)
                        .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));

                int pct = servicio.getPorcentajeEmpleado() != null
                        ? servicio.getPorcentajeEmpleado() : 50;

                BigDecimal subtotal = servicio.getPrecio()
                        .multiply(BigDecimal.valueOf(cantidad))
                        .multiply(BigDecimal.valueOf(pct))
                        .divide(BigDecimal.valueOf(100));

                DetallePagoEmpleado det = new DetallePagoEmpleado();
                det.setPagoEmpleado(pago);
                det.setServicio(servicio);
                det.setCantidad(cantidad);
                det.setSubtotal(subtotal);
                listaDetalles.add(det);

                total = total.add(subtotal);
            }

            if (listaDetalles.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body("Las cantidades deben ser mayores a cero.");
            }

            pago.setDetalles(listaDetalles);
            pago.setMontoTotal(total);

            pagoEmpleadoRepository.save(pago);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Pago al empleado registrado exitosamente.");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    // GET historial de pagos de un empleado
    @GetMapping("/{idEmpleado}")
    public ResponseEntity<?> historial(@PathVariable Integer idEmpleado) {

        List<PagoEmpleado> pagos = pagoEmpleadoRepository
                .findByEmpleado_IdEmpleadoOrderByFechaPagoDesc(idEmpleado);

        List<Map<String, Object>> resultado = pagos.stream().map(p -> {
            Map<String, Object> m = new HashMap<>();
            m.put("idPagoEmpleado", p.getIdPagoEmpleado());
            m.put("fechaPago", p.getFechaPago().toLocalDate().toString());
            m.put("periodoInicio", p.getPeriodoInicio().toString());
            m.put("periodoFin", p.getPeriodoFin().toString());
            m.put("montoTotal", p.getMontoTotal());
            m.put("detalles", p.getDetalles() == null ? List.of() : p.getDetalles().stream().map(d -> {
                Map<String, Object> dm = new HashMap<>();
                dm.put("servicio", d.getServicio().getNombreServicio());
                dm.put("cantidad", d.getCantidad());
                dm.put("subtotal", d.getSubtotal());
                return dm;
            }).toList());
            return m;
        }).toList();

        return ResponseEntity.ok(resultado);
    }
}
