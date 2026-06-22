package co.edu.univalle.NiceLook.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.univalle.NiceLook.model.Arrendamiento;
import co.edu.univalle.NiceLook.service.ArrendamientoService;

/**
 * CRUD de tarifas de arrendamiento (solo ADMIN, ver SecurityConfig).
 */
@RestController
@RequestMapping("/api/arrendamientos")
@CrossOrigin(origins = "*")
public class ArrendamientoController {

    @Autowired private ArrendamientoService service;

    // GET listado completo (default + por estilista + por estilista/servicio)
    @GetMapping
    public ResponseEntity<?> listar() {
        List<Map<String, Object>> response = service.listar().stream()
                .map(this::toMap)
                .toList();
        return ResponseEntity.ok(response);
    }

    // GET valor por defecto del salón
    @GetMapping("/default")
    public ResponseEntity<?> getDefault() {
        Map<String, Object> m = new HashMap<>();
        m.put("valor", service.getValorPorDefecto());
        return ResponseEntity.ok(m);
    }

    // GET previsualización del valor que se aplicaría a un estilista/servicio
    @GetMapping("/resolver")
    public ResponseEntity<?> resolver(
            @RequestParam Integer idEmpleado,
            @RequestParam Integer idServicio) {
        Map<String, Object> m = new HashMap<>();
        m.put("valor", service.resolver(idEmpleado, idServicio));
        return ResponseEntity.ok(m);
    }

    // PUT actualizar valor por defecto del salón
    @PutMapping("/default")
    public ResponseEntity<?> guardarDefault(@RequestBody Map<String, Object> body) {
        try {
            BigDecimal valor = parseValor(body.get("valor"));
            return ResponseEntity.ok(toMap(service.guardarPorDefecto(valor)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // POST crear/actualizar tarifa por estilista o estilista+servicio (upsert)
    @PostMapping
    public ResponseEntity<?> guardar(@RequestBody Map<String, Object> body) {
        try {
            Integer idEmpleado = parseIntOrNull(body.get("idEmpleado"));
            Integer idServicio = parseIntOrNull(body.get("idServicio"));
            BigDecimal valor = parseValor(body.get("valor"));
            return ResponseEntity.ok(toMap(service.guardar(idEmpleado, idServicio, valor)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // DELETE eliminar una sobrescritura (no aplica al valor por defecto)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        try {
            service.eliminar(id);
            return ResponseEntity.ok("Tarifa de arrendamiento eliminada.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ───────────────────────── helpers ─────────────────────────

    private Map<String, Object> toMap(Arrendamiento a) {
        Map<String, Object> m = new HashMap<>();
        m.put("idArrendamiento", a.getIdArrendamiento());
        m.put("valor", a.getValor());
        m.put("fechaActualizacion", a.getFechaActualizacion());

        boolean tieneEmpleado = a.getEmpleado() != null;
        boolean tieneServicio = a.getServicio() != null;

        m.put("idEmpleado", tieneEmpleado ? a.getEmpleado().getIdEmpleado() : null);
        m.put("estilista", tieneEmpleado && a.getEmpleado().getUsuario() != null
                ? a.getEmpleado().getUsuario().getNombreCompleto() : null);
        m.put("idServicio", tieneServicio ? a.getServicio().getIdServicio() : null);
        m.put("servicio", tieneServicio ? a.getServicio().getNombreServicio() : null);

        String tipo = !tieneEmpleado ? "default"
                : (!tieneServicio ? "estilista" : "estilista_servicio");
        m.put("tipo", tipo);

        return m;
    }

    private BigDecimal parseValor(Object raw) {
        if (raw == null || raw.toString().isBlank()) {
            throw new RuntimeException("Debes indicar el valor de arrendamiento.");
        }
        try {
            return new BigDecimal(raw.toString().trim());
        } catch (NumberFormatException e) {
            throw new RuntimeException("El valor de arrendamiento no es un número válido.");
        }
    }

    private Integer parseIntOrNull(Object raw) {
        if (raw == null || raw.toString().isBlank()) return null;
        return Integer.valueOf(raw.toString().trim());
    }
}
