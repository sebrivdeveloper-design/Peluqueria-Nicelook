package co.edu.univalle.NiceLook.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.univalle.NiceLook.model.Cita;
import co.edu.univalle.NiceLook.model.Empleado;
import co.edu.univalle.NiceLook.repository.CitaRepository;
import co.edu.univalle.NiceLook.repository.EmpleadoRepository;
import co.edu.univalle.NiceLook.repository.PagoRepository;
import co.edu.univalle.NiceLook.service.ArrendamientoService;

/**
 * Historial de servicios y pagos por estilista (CHANGE 2 - V2).
 *
 * Reemplaza el módulo previo de "pago a empleados por porcentaje": el reparto ahora
 * se basa en la comisión por arrendamiento congelada en cada cita cobrada.
 *
 * - {@code /api/historial-estilistas/{idEmpleado}} : solo ADMIN, cualquier estilista.
 * - {@code /api/historial-estilistas/mio}          : el estilista autenticado, solo su propio historial.
 */
@RestController
@RequestMapping("/api/historial-estilistas")
@CrossOrigin(origins = "*")
public class HistorialEstilistaController {

    @Autowired private CitaRepository citaRepository;
    @Autowired private PagoRepository pagoRepository;
    @Autowired private EmpleadoRepository empleadoRepository;
    @Autowired private ArrendamientoService arrendamientoService;

    // ADMIN: historial de cualquier estilista
    @GetMapping("/{idEmpleado}")
    public ResponseEntity<?> historialDe(
            @PathVariable Integer idEmpleado,
            @RequestParam String desde,
            @RequestParam String hasta) {

        Empleado empleado = empleadoRepository.findById(idEmpleado).orElse(null);
        if (empleado == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Estilista no encontrado.");
        }
        return ResponseEntity.ok(construir(empleado, LocalDate.parse(desde), LocalDate.parse(hasta)));
    }

    // ESTILISTA: su propio historial (resuelto desde la sesión, no se confía en un id externo)
    @GetMapping("/mio")
    public ResponseEntity<?> miHistorial(
            Authentication auth,
            @RequestParam String desde,
            @RequestParam String hasta) {

        Empleado empleado = empleadoRepository.findByUsuario_Correo(auth.getName()).orElse(null);
        if (empleado == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontró un estilista asociado a tu cuenta.");
        }
        return ResponseEntity.ok(construir(empleado, LocalDate.parse(desde), LocalDate.parse(hasta)));
    }

    // ───────────────────────── construcción del historial ─────────────────────────

    private Map<String, Object> construir(Empleado empleado, LocalDate desde, LocalDate hasta) {

        List<Cita> citas = citaRepository.findFinalizadasPorEmpleadoEntre(
                empleado.getIdEmpleado(), desde, hasta);

        List<Map<String, Object>> filas = new ArrayList<>();

        BigDecimal totalFacturado = BigDecimal.ZERO;
        BigDecimal totalArrendamiento = BigDecimal.ZERO;
        BigDecimal totalAPagar = BigDecimal.ZERO;

        for (Cita c : citas) {
            boolean pagada = pagoRepository.existsByCita_IdCitaAndEstadoPago(c.getIdCita(), "completado");

            BigDecimal valorServicio;
            BigDecimal valorArrendamiento;
            BigDecimal valorAPagar;

            if (pagada && c.getValorServicio() != null) {
                // Valores CONGELADOS en el cobro (históricos e inmutables)
                valorServicio = c.getValorServicio();
                valorArrendamiento = c.getValorArrendamiento();
                valorAPagar = c.getValorAPagarEstilista();
            } else {
                // Servicio realizado pero aún sin cobrar: estimación con tarifas vigentes
                valorServicio = c.getServicio().getPrecio();
                valorArrendamiento = arrendamientoService.resolver(
                        empleado.getIdEmpleado(), c.getServicio().getIdServicio());
                valorAPagar = valorServicio.subtract(valorArrendamiento);
            }

            Map<String, Object> m = new HashMap<>();
            m.put("idCita", c.getIdCita());
            m.put("fecha", c.getFechaCita().toString());
            m.put("hora", c.getHoraInicio().toString());
            m.put("cliente", c.getCliente().getUsuario() != null
                    ? c.getCliente().getUsuario().getNombreCompleto() : "");
            m.put("servicio", c.getServicio().getNombreServicio());
            m.put("valorServicio", valorServicio);
            m.put("valorArrendamiento", valorArrendamiento);
            m.put("valorAPagarEstilista", valorAPagar);
            m.put("estadoPago", pagada ? "pagado" : "pendiente");
            filas.add(m);

            totalFacturado = totalFacturado.add(valorServicio);
            totalArrendamiento = totalArrendamiento.add(valorArrendamiento);
            totalAPagar = totalAPagar.add(valorAPagar);
        }

        Map<String, Object> resumen = new HashMap<>();
        resumen.put("totalServicios", filas.size());
        resumen.put("totalFacturado", totalFacturado);
        resumen.put("totalArrendamiento", totalArrendamiento);
        resumen.put("totalAPagarEstilista", totalAPagar);

        Map<String, Object> response = new HashMap<>();
        response.put("idEmpleado", empleado.getIdEmpleado());
        response.put("estilista", empleado.getUsuario() != null
                ? empleado.getUsuario().getNombreCompleto() : "");
        response.put("filas", filas);
        response.put("resumen", resumen);
        return response;
    }
}
