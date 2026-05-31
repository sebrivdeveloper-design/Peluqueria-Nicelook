package co.edu.univalle.NiceLook.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import co.edu.univalle.NiceLook.DTO.BaseDiariaDTO;
import co.edu.univalle.NiceLook.service.BaseDiariaService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/base-diaria")
@CrossOrigin(origins = "*")
public class BaseDiariaController {

    @Autowired
    private BaseDiariaService baseDiariaService;

    @PostMapping
    public ResponseEntity<?> registrarBase(
            @Valid @RequestBody BaseDiariaDTO dto) {

        try {

            baseDiariaService.registrarBase(
                    dto.getMontoInicial());

            return ResponseEntity.ok(
                    "Base registrada correctamente");

        } catch (Exception e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    @GetMapping("/hoy")
    public ResponseEntity<?> obtenerBaseHoy() {

        return ResponseEntity.ok(
                baseDiariaService.obtenerBaseHoy());
    }
}