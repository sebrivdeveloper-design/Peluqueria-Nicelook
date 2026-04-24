package co.edu.univalle.NiceLook.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.List;
import co.edu.univalle.NiceLook.service.CategoriaService;
import co.edu.univalle.NiceLook.model.CategoriaServicios;
import co.edu.univalle.NiceLook.model.Servicio;
import co.edu.univalle.NiceLook.repository.CategoriaRepository;
import co.edu.univalle.NiceLook.service.ServicioService;

@RestController
@RequestMapping("/api/categorias")
@CrossOrigin(origins = "*")
public class CategoriaController {

    @Autowired
    private CategoriaRepository repository;
    @Autowired
    private CategoriaService service;
    @Autowired
    private ServicioService servicioService;

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody CategoriaServicios categoria) {
        try {
            return ResponseEntity.ok(service.crearCategoria(categoria));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public List<CategoriaServicios> listar() {
        return service.listar();
    }

    @GetMapping("/categoria/{id}")
    public List<Servicio> listarPorCategoria(@PathVariable Long id) {
        return servicioService.listar();
    }

    @GetMapping("/{idCategoria}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long idCategoria) {
    return repository.findById(idCategoria)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
}
    
    @PutMapping("/{idCategoria}")
    public ResponseEntity<?> actualizar(@PathVariable Long idCategoria, @RequestBody CategoriaServicios categoria) {
        try {
            return ResponseEntity.ok(service.actualizar(idCategoria, categoria));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{idCategoria}")
    public ResponseEntity<?> deshabilitar(@PathVariable Long idCategoria) {
        service.deshabilitar(idCategoria);
        return ResponseEntity.ok("Categoría deshabilitada");
    }

    // 🔥 OPCIONAL
    @PutMapping("/activar/{idCategoria}")
    public void activar(@PathVariable Long idCategoria) {
        service.activar(idCategoria);
    }
}