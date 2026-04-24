package co.edu.univalle.NiceLook.controller;

import co.edu.univalle.NiceLook.model.Servicio;
import co.edu.univalle.NiceLook.service.ServicioService;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/servicios")
@CrossOrigin("*")
public class ServicioController {

    @Autowired
    private ServicioService servicioService;

    // 🔥 ESTE ES EL QUE TE FALTABA
    @GetMapping
    public List<Servicio> listar() {
        return servicioService.listar();
    }

    // 🔥 LISTAR POR CATEGORÍA
    @GetMapping("/categoria/{idCategoria}")
    public List<Servicio> listarPorCategoria(@PathVariable Long idCategoria) {
        return servicioService.listarPorCategoria(idCategoria);
    }

    // 🔥 CREAR
    @PostMapping
    public ResponseEntity<?> guardar(@Valid @RequestBody Servicio servicio) {
        Servicio nuevo = servicioService.guardar(servicio);
        return ResponseEntity.ok(nuevo);
    }
}