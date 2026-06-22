package co.edu.univalle.NiceLook.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import co.edu.univalle.NiceLook.DTO.EmpleadoRequest;
import co.edu.univalle.NiceLook.model.Empleado;
import co.edu.univalle.NiceLook.service.EmpleadoService;

@RestController
@RequestMapping("/api/empleados")
@CrossOrigin(origins = "*")
public class EmpleadoController {

    @Autowired
    private EmpleadoService service;

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody EmpleadoRequest request) {
        try {
            Empleado empleado = service.crearEmpleado(request);
            return ResponseEntity.ok(empleado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(
            @PathVariable Integer id,
            @RequestBody EmpleadoRequest request) {
        try {
            Empleado empleado = service.editarEmpleado(id, request);
            return ResponseEntity.ok(empleado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public List<Empleado> listar() {
        return service.listar();
    }

    @PutMapping("/{id}/desactivar")
    public ResponseEntity<?> desactivar(@PathVariable Integer id) {
        try {
            service.cambiarEstado(id, false);
            return ResponseEntity.ok("Empleado desactivado. Ya no podrá iniciar sesión.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/activar")
    public ResponseEntity<?> activar(@PathVariable Integer id) {
        try {
            service.cambiarEstado(id, true);
            return ResponseEntity.ok("Empleado reactivado exitosamente.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
