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

    // 🔥 CREAR
    public CategoriaServicios crearCategoria(CategoriaServicios categoria) {

        if (repository.findByNombreCategoria(categoria.getNombreCategoria()).isPresent()) {
            throw new RuntimeException("La categoría ya existe");
        }

        return repository.save(categoria);
    }

    // 🔥 OBTENER POR ID
    public CategoriaServicios obtenerPorId(Long idCategoria) {
        return repository.findById(idCategoria)
                .orElseThrow(() -> new RuntimeException("No existe la categoría"));
    }

    // 🔥 ACTUALIZAR
    public CategoriaServicios actualizar(Long idCategoria, CategoriaServicios categoria) {

        CategoriaServicios existente = repository.findById(idCategoria)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        existente.setNombreCategoria(categoria.getNombreCategoria());
        existente.setDescripcion(categoria.getDescripcion());

        return repository.save(existente);
    }

    // 🔥 LISTAR ACTIVAS
    public List<CategoriaServicios> listar() {
        return repository.findByActivoTrue();
    }

    // 🔥 SOFT DELETE
    public void deshabilitar(Long idCategoria) {
        CategoriaServicios categoria = repository.findById(idCategoria)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        categoria.setActivo(false);
        repository.save(categoria);
    }

    // 🔥 ACTIVAR
    public void activar(Long idCategoria) {
        CategoriaServicios categoria = repository.findById(idCategoria)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        categoria.setActivo(true);
        repository.save(categoria);
    }

    // 🔥 CLAVE: LISTAR SERVICIOS POR CATEGORÍA
    public List<Servicio> listarPorCategoria(Long idCategoria) {
        return servicioRepository.findByCategoria_IdCategoria(idCategoria)
                .stream()
                .filter(s -> "activo".equals(s.getEstado()))
                .toList();
    }
}