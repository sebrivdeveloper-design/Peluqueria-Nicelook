package co.edu.univalle.NiceLook.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import co.edu.univalle.NiceLook.repository.CategoriaRepository;
import co.edu.univalle.NiceLook.model.CategoriaServicios;
import co.edu.univalle.NiceLook.model.Servicio;
import co.edu.univalle.NiceLook.repository.ServicioRepository;

@Service
public class CategoriaService {
    @Autowired
    private CategoriaRepository repository;

    @Autowired
    private ServicioRepository servicioRepository;

    public CategoriaServicios crearCategoria(CategoriaServicios categoria) {

        // Validación: nombre único
        if (repository.findByNombreCategoria(categoria.getNombreCategoria()).isPresent()) {
            throw new RuntimeException("La categoría ya existe");
        }

        return repository.save(categoria);
    }

    public CategoriaServicios obtenerPorId(Long idCategoria) {
    return repository.findById(idCategoria)
        .orElseThrow(() -> new RuntimeException("No existe la categoría"));
}

    public CategoriaServicios actualizar(Long idCategoria, CategoriaServicios categoria) {
        CategoriaServicios existente = repository.findById(idCategoria).orElseThrow();
        existente.setNombreCategoria(categoria.getNombreCategoria());
        existente.setDescripcion(categoria.getDescripcion());
        
        return repository.save(existente);
    }

    public List<CategoriaServicios> listar() {
        return repository.findByActivoTrue();
    }

     // 🔥 SOFT DELETE
    public void deshabilitar(Long idCategoria) {
        CategoriaServicios categoria = repository.findById(idCategoria).orElseThrow();
        categoria.setActivo(false);
        repository.save(categoria);
    }

    // 🔥 OPCIONAL (PRO)
    public void activar(Long idCategoria) {
        CategoriaServicios categoria = repository.findById(idCategoria).orElseThrow();
        categoria.setActivo(true);
        repository.save(categoria);
    }

    public List<Servicio> listarPorCategoria(Long idCategoria) {
        return servicioRepository.findByCategoriaIdCategoria(idCategoria)
            .stream()
            .filter(s -> "activo".equals(s.getEstado()))
            .toList();
    }
}