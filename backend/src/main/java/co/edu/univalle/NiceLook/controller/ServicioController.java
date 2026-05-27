package co.edu.univalle.NiceLook.controller;

import java.util.List;

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

import co.edu.univalle.NiceLook.model.Servicio;
import co.edu.univalle.NiceLook.repository.ServicioRepository;
import co.edu.univalle.NiceLook.service.ServicioService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/servicios")
@CrossOrigin("*")
public class ServicioController {

    @Autowired
    private ServicioRepository servicioRepository;

    @Autowired
    private ServicioService servicioService;

    //Método de busqueda del servicio
    @GetMapping("/buscar")
    public ResponseEntity<?> buscar(@RequestParam String texto) {
        //restricción para el campo de búsqueda
        if (texto.length() > 30) {
            return ResponseEntity.badRequest().body("La búsqueda no puede exceder los 30 caracteres.");
        }

        
        if (!texto.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]*$")) {
            return ResponseEntity.badRequest().body("Solo se permiten caracteres alfabéticos.");
        }

        
        // HU-18: Solo mostrar servicios activos en la búsqueda del cliente
        List<Servicio> resultados = servicioRepository.findByNombreServicioContainingIgnoreCaseAndEstado(texto, "activo");

        return ResponseEntity.ok(resultados);
    }

    

    @GetMapping("/admin")
    public List<Servicio> listarAdmin() {
        return servicioService.listarTodos();
    }

    @GetMapping
    public List<Servicio> listar() {
        return servicioService.listarActivos();
    }

    @GetMapping("/categoria/{idCategoria}")
    public List<Servicio> listarPorCategoria(@PathVariable Long idCategoria) {
        return servicioService.listarPorCategoria(idCategoria);
    }

    @PostMapping
    public ResponseEntity<?> guardar(@Valid @RequestBody Servicio servicio) {
        try {
            Servicio nuevo = servicioService.guardar(servicio);
            return ResponseEntity.ok(nuevo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Integer id) {
        return servicioRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deshabilitar(@PathVariable Integer id) {
        try {
            servicioService.deshabilitar(id);
            return ResponseEntity.ok("Servicio desactivado");
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/activar/{id}")
    public ResponseEntity<?> activar(@PathVariable Integer id) {
        try {
            servicioService.activar(id);
            return ResponseEntity.ok("Servicio activado");
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Integer id, @Valid @RequestBody Servicio servicio) {
        try {
            return servicioRepository.findById(id)
                .map(s -> {
                    s.setNombreServicio(servicio.getNombreServicio());
                    s.setDescripcion(servicio.getDescripcion());
                    s.setDuracion(servicio.getDuracion());
                    s.setPrecio(servicio.getPrecio());
                    s.setCategoria(servicio.getCategoria());
                    return ResponseEntity.ok(servicioService.guardar(s));
                })
                .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}