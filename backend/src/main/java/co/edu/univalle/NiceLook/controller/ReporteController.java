package co.edu.univalle.NiceLook.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.univalle.NiceLook.model.CajaBase;
import co.edu.univalle.NiceLook.model.Cita;
import co.edu.univalle.NiceLook.model.Pago;
import co.edu.univalle.NiceLook.repository.CajaBaseRepository;
import co.edu.univalle.NiceLook.repository.CitaRepository;
import co.edu.univalle.NiceLook.repository.PagoRepository;

@RestController
@RequestMapping("/api/reportes")
@CrossOrigin(origins = "*")
public class ReporteController {

    @Autowired private CitaRepository citaRepository;
    @Autowired private PagoRepository pagoRepository;
    @Autowired private CajaBaseRepository cajaBaseRepository;

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

    // ─────────────────────────────────────────────────────────────
    // CHANGE 3 - V2 — Saldos de caja por día (vista mensual + detalle)
    // ─────────────────────────────────────────────────────────────

    /**
     * Saldos por día de un mes: una fila por cada día con movimiento (pagos completados),
     * con desglose por método de pago y el saldo final (base de caja + total recibido).
     */
    @GetMapping("/saldos-diarios")
    public ResponseEntity<?> saldosDiarios(
            @RequestParam int anio,
            @RequestParam int mes) {

        try {
            YearMonth ym = YearMonth.of(anio, mes);
            LocalDate inicio = ym.atDay(1);
            LocalDate fin = ym.atEndOfMonth();

            List<Pago> pagos = pagoRepository.findByFechaPagoBetweenOrderByFechaPagoDesc(
                    inicio.atStartOfDay(), fin.atTime(23, 59, 59));

            // Agrupar por día (solo pagos completados). TreeMap → días ordenados asc.
            Map<LocalDate, List<Pago>> porDia = new TreeMap<>();
            for (Pago p : pagos) {
                if (!"completado".equalsIgnoreCase(p.getEstadoPago())) continue;
                LocalDate dia = p.getFechaPago().toLocalDate();
                porDia.computeIfAbsent(dia, k -> new ArrayList<>()).add(p);
            }

            List<Map<String, Object>> dias = new ArrayList<>();
            for (Map.Entry<LocalDate, List<Pago>> e : porDia.entrySet()) {
                LocalDate dia = e.getKey();
                List<Pago> delDia = e.getValue();

                BigDecimal total = BigDecimal.ZERO;
                BigDecimal efectivo = BigDecimal.ZERO;
                BigDecimal tarjeta = BigDecimal.ZERO;
                BigDecimal transferencia = BigDecimal.ZERO;

                for (Pago p : delDia) {
                    BigDecimal monto = BigDecimal.valueOf(p.getMontoTotal());
                    total = total.add(monto);
                    switch (p.getMetodoPago() == null ? "" : p.getMetodoPago().toLowerCase()) {
                        case "efectivo" -> efectivo = efectivo.add(monto);
                        case "tarjeta" -> tarjeta = tarjeta.add(monto);
                        case "transferencia" -> transferencia = transferencia.add(monto);
                        default -> { }
                    }
                }

                BigDecimal base = cajaBaseRepository.findByFecha(dia)
                        .map(CajaBase::getMontoBase).orElse(BigDecimal.ZERO);

                Map<String, Object> m = new HashMap<>();
                m.put("fecha", dia.toString());
                m.put("cantidadServicios", delDia.size());
                m.put("efectivo", efectivo);
                m.put("tarjeta", tarjeta);
                m.put("transferencia", transferencia);
                m.put("totalRecibido", total);
                m.put("baseCaja", base);
                m.put("saldoFinal", base.add(total));
                dias.add(m);
            }

            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("anio", anio);
            respuesta.put("mes", mes);
            respuesta.put("dias", dias);
            return ResponseEntity.ok(respuesta);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Detalle de un día: cada servicio cobrado (con reparto por arrendamiento) y los
     * totales del día (facturado, arrendamiento/ingreso neto del salón, pagado a estilistas,
     * base de caja y saldo final).
     */
    @GetMapping("/saldos-dia")
    public ResponseEntity<?> saldosDia(@RequestParam String fecha) {

        try {
            LocalDate dia = LocalDate.parse(fecha);

            List<Pago> pagos = pagoRepository.findByFechaPagoBetweenOrderByFechaPagoDesc(
                    dia.atStartOfDay(), dia.atTime(23, 59, 59));

            List<Map<String, Object>> filas = new ArrayList<>();
            BigDecimal totalFacturado = BigDecimal.ZERO;
            BigDecimal totalArrendamiento = BigDecimal.ZERO;
            BigDecimal totalPagadoEstilistas = BigDecimal.ZERO;

            for (Pago p : pagos) {
                if (!"completado".equalsIgnoreCase(p.getEstadoPago())) continue;

                Cita c = p.getCita();
                // Valores congelados en la cita (CHANGE 1); fallback para pagos antiguos.
                BigDecimal valorServicio = c.getValorServicio() != null
                        ? c.getValorServicio() : BigDecimal.valueOf(p.getMontoTotal());
                BigDecimal valorArrendamiento = c.getValorArrendamiento() != null
                        ? c.getValorArrendamiento() : BigDecimal.ZERO;
                BigDecimal valorAPagar = c.getValorAPagarEstilista() != null
                        ? c.getValorAPagarEstilista() : valorServicio.subtract(valorArrendamiento);

                Map<String, Object> m = new HashMap<>();
                m.put("hora", p.getFechaPago().toLocalTime().toString());
                m.put("cliente", c.getCliente().getUsuario() != null
                        ? c.getCliente().getUsuario().getNombreCompleto() : "");
                m.put("estilista", c.getEmpleado().getUsuario() != null
                        ? c.getEmpleado().getUsuario().getNombreCompleto() : "");
                m.put("servicio", c.getServicio().getNombreServicio());
                m.put("valorServicio", valorServicio);
                m.put("valorArrendamiento", valorArrendamiento);
                m.put("valorAPagarEstilista", valorAPagar);
                m.put("metodoPago", p.getMetodoPago());
                filas.add(m);

                totalFacturado = totalFacturado.add(valorServicio);
                totalArrendamiento = totalArrendamiento.add(valorArrendamiento);
                totalPagadoEstilistas = totalPagadoEstilistas.add(valorAPagar);
            }

            BigDecimal base = cajaBaseRepository.findByFecha(dia)
                    .map(CajaBase::getMontoBase).orElse(BigDecimal.ZERO);

            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("fecha", dia.toString());
            respuesta.put("filas", filas);
            respuesta.put("baseCaja", base);
            respuesta.put("totalFacturado", totalFacturado);
            respuesta.put("totalArrendamiento", totalArrendamiento);
            respuesta.put("totalPagadoEstilistas", totalPagadoEstilistas);
            respuesta.put("saldoFinal", base.add(totalFacturado));
            return ResponseEntity.ok(respuesta);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
