package co.edu.univalle.NiceLook.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.univalle.NiceLook.model.Cita;
import co.edu.univalle.NiceLook.model.Pago;
import co.edu.univalle.NiceLook.repository.CitaRepository;
import co.edu.univalle.NiceLook.repository.PagoRepository;

@RestController
@RequestMapping("/api/reportes")
@CrossOrigin(origins = "*")
public class ReporteController {

    @Autowired private CitaRepository citaRepository;
    @Autowired private PagoRepository pagoRepository;

    @GetMapping
    public ResponseEntity<?> reporte(
            @RequestParam String desde,
            @RequestParam String hasta) {

        try {

            LocalDate dDesde = LocalDate.parse(desde);
            LocalDate dHasta = LocalDate.parse(hasta);

            // 1. Total de ingresos (pagos completados en el rango)
            List<Pago> pagos = pagoRepository.findByFechaPagoBetweenOrderByFechaPagoDesc(
                    dDesde.atStartOfDay(),
                    dHasta.atTime(23, 59, 59));

            double totalIngresos = pagos.stream()
                    .filter(p -> "completado".equalsIgnoreCase(p.getEstadoPago()))
                    .mapToDouble(Pago::getMontoTotal)
                    .sum();

            // 2. Citas finalizadas del periodo para servicios top y desempeño
            List<Cita> finalizadas = citaRepository.findFinalizadasEntre(dDesde, dHasta);

            // Servicios más solicitados
            Map<String, Integer> conteoServicios = new HashMap<>();
            // Desempeño por barbero: citas atendidas y monto generado
            Map<String, Integer> citasPorBarbero = new HashMap<>();
            Map<String, BigDecimal> montoPorBarbero = new HashMap<>();

            for (Cita c : finalizadas) {
                String servicio = c.getServicio().getNombreServicio();
                conteoServicios.merge(servicio, 1, Integer::sum);

                String barbero = c.getEmpleado().getUsuario() != null
                        ? c.getEmpleado().getUsuario().getNombreCompleto() : "Sin nombre";
                citasPorBarbero.merge(barbero, 1, Integer::sum);
                montoPorBarbero.merge(barbero, c.getServicio().getPrecio(), BigDecimal::add);
            }

            List<Map<String, Object>> serviciosTop = new ArrayList<>();
            conteoServicios.forEach((nombre, cantidad) -> {
                Map<String, Object> m = new HashMap<>();
                m.put("nombre", nombre);
                m.put("cantidad", cantidad);
                serviciosTop.add(m);
            });
            serviciosTop.sort(Comparator.comparingInt(m -> -(Integer) m.get("cantidad")));

            List<Map<String, Object>> desempeno = new ArrayList<>();
            citasPorBarbero.forEach((barbero, cantidad) -> {
                Map<String, Object> m = new HashMap<>();
                m.put("nombre", barbero);
                m.put("citasAtendidas", cantidad);
                m.put("montoGenerado", montoPorBarbero.getOrDefault(barbero, BigDecimal.ZERO));
                desempeno.add(m);
            });
            desempeno.sort(Comparator.comparingInt(m -> -(Integer) m.get("citasAtendidas")));

            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("totalIngresos", totalIngresos);
            respuesta.put("totalCitas", finalizadas.size());
            respuesta.put("serviciosTop", serviciosTop);
            respuesta.put("desempenoBarberos", desempeno);

            return ResponseEntity.ok(respuesta);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }
}
