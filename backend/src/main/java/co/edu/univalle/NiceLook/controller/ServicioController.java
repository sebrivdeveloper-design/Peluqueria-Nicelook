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

    // ADMIN
    @GetMapping("/admin")
    public List<Servicio> listarAdmin() {
        return servicioService.listarTodos();
    }

    // Cliente
    @GetMapping
    public List<Servicio> listar() {
        return servicioService.listarActivos();
    }

    // LISTAR POR CATEGORÍA
    @GetMapping("/categoria/{idCategoria}")
    public List<Servicio> listarPorCategoria(@PathVariable Long idCategoria) {
        return servicioService.listarPorCategoria(idCategoria);
    }

    // GUARDAR
    @PostMapping
    public ResponseEntity<?> guardar(@Valid @RequestBody Servicio servicio) {
        Servicio nuevo = servicioService.guardar(servicio);
        return ResponseEntity.ok(nuevo);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Integer id) {
        return servicioService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deshabilitar(@PathVariable Integer id) {
        servicioService.deshabilitar(id);
        return ResponseEntity.ok("Servicio desactivado");
    }

    @PutMapping("/activar/{id}")
    public ResponseEntity<?> activar(@PathVariable Integer id) {
        servicioService.activar(id);
        return ResponseEntity.ok("Servicio activado");
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Integer id, @Valid @RequestBody Servicio servicio) {
        try {
            return ResponseEntity.ok(servicioService.actualizar(id, servicio));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}