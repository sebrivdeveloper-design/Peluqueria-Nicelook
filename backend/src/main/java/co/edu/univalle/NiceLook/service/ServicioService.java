package co.edu.univalle.NiceLook.service;

import co.edu.univalle.NiceLook.model.Servicio;
import co.edu.univalle.NiceLook.repository.ServicioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServicioService {

    @Autowired
    private ServicioRepository servicioRepository;

    // 🔥 LISTAR TODOS
    public List<Servicio> listar() {
        return servicioRepository.findAll()
                .stream()
                .filter(s -> "activo".equals(s.getEstado()))
                .toList();
    }

    // 🔥 LISTAR POR CATEGORÍA (CLAVE)
    public List<Servicio> listarPorCategoria(Long idCategoria) {
        return servicioRepository.findByCategoria_IdCategoria(idCategoria)
                .stream()
                .filter(s -> "activo".equals(s.getEstado()))
                .toList();
    }

    // 🔥 GUARDAR
    public Servicio guardar(Servicio servicio) {
        servicio.setEstado("activo");
        return servicioRepository.save(servicio);
    }
}