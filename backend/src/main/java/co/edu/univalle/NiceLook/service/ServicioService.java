package co.edu.univalle.NiceLook.service;

import co.edu.univalle.NiceLook.model.CategoriaServicios;
import co.edu.univalle.NiceLook.model.Servicio;
import co.edu.univalle.NiceLook.repository.CategoriaRepository;
import co.edu.univalle.NiceLook.repository.ServicioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServicioService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private ServicioRepository servicioRepository;

    public List<Servicio> listarTodos() {
        return servicioRepository.findAll();
    }

    public List<Servicio> listarActivos() {
        return servicioRepository.findByEstado("activo");

    }

    // LISTAR POR CATEGORÍA
    public List<Servicio> listarPorCategoria(Long idCategoria) {
        return servicioRepository.findByCategoria_IdCategoria(idCategoria)
                .stream()
                .filter(s -> "activo".equals(s.getEstado()))
                .toList();
    }

    // GUARDAR
    public Servicio guardar(Servicio servicio) {

        CategoriaServicios categoria = categoriaRepository
                .findById(servicio.getCategoria().getIdCategoria())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        if (!categoria.getActivo()) {
            throw new RuntimeException("No se pueden crear servicios en una categoría inactiva");
        }

        servicio.setEstado("activo");
        return servicioRepository.save(servicio);
    }

    public void deshabilitar(Integer id) {
        Servicio s = servicioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Servicio no existe"));

        s.setEstado("inactivo");
        servicioRepository.save(s);
    }

    public void activar(Integer id) {
        Servicio s = servicioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Servicio no existe"));

        s.setEstado("activo");
        servicioRepository.save(s);
    }

}