package co.edu.univalle.NiceLook.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.univalle.NiceLook.model.CajaBase;
import co.edu.univalle.NiceLook.repository.CajaBaseRepository;
import co.edu.univalle.NiceLook.repository.PagoRepository;

@RestController
@RequestMapping("/api/caja")
@CrossOrigin(origins = "*")
public class CajaController {

    @Autowired
    private CajaBaseRepository cajaBaseRepository;

    @Autowired
    private PagoRepository pagoRepository;

    // POST registrar base del día
    @PostMapping
    public ResponseEntity<?> registrarBase(@RequestBody Map<String, String> body) {

        try {

            String montoStr = body.get("montoBase");

            if (montoStr == null || montoStr.isBlank()) {
                return ResponseEntity
                        .badRequest()
                        .body("Debes ingresar el monto de la base.");
            }

            BigDecimal monto;
            try {
                monto = new BigDecimal(montoStr.trim());
            } catch (NumberFormatException e) {
                return ResponseEntity
                        .badRequest()
                        .body("El monto debe ser un valor numérico.");
            }

            if (monto.compareTo(BigDecimal.ZERO) <= 0) {
                return ResponseEntity
                        .badRequest()
                        .body("El monto debe ser mayor a cero.");
            }

            if (monto.precision() - monto.scale() > 8) {
                return ResponseEntity
                        .badRequest()
                        .body("El monto no puede superar 8 dígitos.");
            }

            LocalDate hoy = LocalDate.now();

            if (cajaBaseRepository.findByFecha(hoy).isPresent()) {
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body("Ya se registró la base del día de hoy.");
            }

            CajaBase caja = new CajaBase();
            caja.setFecha(hoy);
            caja.setMontoBase(monto);
            caja.setFechaRegistro(LocalDateTime.now());

            cajaBaseRepository.save(caja);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body("Base diaria registrada exitosamente.");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    // GET base de hoy
    @GetMapping("/hoy")
    public ResponseEntity<?> baseDeHoy() {

        return cajaBaseRepository.findByFecha(LocalDate.now())
                .<ResponseEntity<?>>map(caja -> ResponseEntity.ok(Map.of(
                        "idCaja", caja.getIdCaja(),
                        "fecha", caja.getFecha().toString(),
                        "montoBase", caja.getMontoBase())))
                .orElse(ResponseEntity.notFound().build());
    }

    // GET cierre del día: base + ingresos válidos + total esperado
    @GetMapping("/cierre")
    public ResponseEntity<?> cierre(@RequestParam(required = false) String fecha) {

        try {

            LocalDate dia = (fecha == null || fecha.isBlank())
                    ? LocalDate.now()
                    : LocalDate.parse(fecha);

            BigDecimal base = cajaBaseRepository.findByFecha(dia)
                    .map(CajaBase::getMontoBase)
                    .orElse(BigDecimal.ZERO);

            Double ingresos = pagoRepository.totalCompletadosEntre(
                    dia.atStartOfDay(),
                    dia.atTime(23, 59, 59));

            BigDecimal totalIngresos = BigDecimal.valueOf(ingresos != null ? ingresos : 0);

            return ResponseEntity.ok(Map.of(
                    "fecha", dia.toString(),
                    "baseInicial", base,
                    "totalIngresos", totalIngresos,
                    "totalEsperado", base.add(totalIngresos)));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }
}
