package co.edu.univalle.NiceLook.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.univalle.NiceLook.model.Arrendamiento;
import co.edu.univalle.NiceLook.model.Empleado;
import co.edu.univalle.NiceLook.model.Servicio;
import co.edu.univalle.NiceLook.repository.ArrendamientoRepository;
import co.edu.univalle.NiceLook.repository.EmpleadoRepository;
import co.edu.univalle.NiceLook.repository.ServicioRepository;

/**
 * Gestión y resolución de las tarifas de arrendamiento del salón.
 * Ver {@link Arrendamiento} para el modelo de los tres niveles.
 */
@Service
public class ArrendamientoService {

    @Autowired private ArrendamientoRepository arrendamientoRepository;
    @Autowired private EmpleadoRepository empleadoRepository;
    @Autowired private ServicioRepository servicioRepository;

    /**
     * Resuelve el valor de arrendamiento aplicable a un servicio realizado por un
     * estilista, de más específico a más general:
     *   1. estilista + servicio  ->  2. estilista  ->  3. valor por defecto del salón.
     * Si nada está configurado, retorna 0 (el salón no retiene nada).
     */
    public BigDecimal resolver(Integer idEmpleado, Integer idServicio) {

        Optional<Arrendamiento> especifico =
                arrendamientoRepository.findByEmpleado_IdEmpleadoAndServicio_IdServicio(idEmpleado, idServicio);
        if (especifico.isPresent()) return especifico.get().getValor();

        Optional<Arrendamiento> porEstilista =
                arrendamientoRepository.findByEmpleado_IdEmpleadoAndServicioIsNull(idEmpleado);
        if (porEstilista.isPresent()) return porEstilista.get().getValor();

        return getValorPorDefecto();
    }

    /** Valor por defecto del salón (0 si aún no se ha configurado). */
    public BigDecimal getValorPorDefecto() {
        return arrendamientoRepository.findByEmpleadoIsNullAndServicioIsNull()
                .map(Arrendamiento::getValor)
                .orElse(BigDecimal.ZERO);
    }

    public List<Arrendamiento> listar() {
        return arrendamientoRepository.findAllByOrderByEmpleado_IdEmpleadoAscServicio_IdServicioAsc();
    }

    /** Crea o actualiza el valor por defecto del salón. */
    public Arrendamiento guardarPorDefecto(BigDecimal valor) {
        validarValor(valor);
        Arrendamiento config = arrendamientoRepository.findByEmpleadoIsNullAndServicioIsNull()
                .orElseGet(Arrendamiento::new);
        config.setEmpleado(null);
        config.setServicio(null);
        config.setValor(valor);
        config.setFechaActualizacion(LocalDateTime.now());
        return arrendamientoRepository.save(config);
    }

    /**
     * Crea o actualiza una tarifa por estilista (idServicio = null) o por
     * combinación estilista+servicio (idServicio != null). Upsert: si ya existe
     * una configuración para esa misma especificidad, se actualiza su valor.
     */
    public Arrendamiento guardar(Integer idEmpleado, Integer idServicio, BigDecimal valor) {
        validarValor(valor);

        if (idEmpleado == null) {
            // Sin estilista equivale al valor por defecto del salón
            return guardarPorDefecto(valor);
        }

        Empleado empleado = empleadoRepository.findById(idEmpleado)
                .orElseThrow(() -> new RuntimeException("Estilista no encontrado"));

        Servicio servicio = null;
        Optional<Arrendamiento> existente;

        if (idServicio == null) {
            existente = arrendamientoRepository.findByEmpleado_IdEmpleadoAndServicioIsNull(idEmpleado);
        } else {
            servicio = servicioRepository.findById(idServicio)
                    .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));
            existente = arrendamientoRepository.findByEmpleado_IdEmpleadoAndServicio_IdServicio(idEmpleado, idServicio);
        }

        Arrendamiento config = existente.orElseGet(Arrendamiento::new);
        config.setEmpleado(empleado);
        config.setServicio(servicio);
        config.setValor(valor);
        config.setFechaActualizacion(LocalDateTime.now());
        return arrendamientoRepository.save(config);
    }

    public void eliminar(Integer id) {
        Arrendamiento config = arrendamientoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Configuración de arrendamiento no encontrada"));
        // El valor por defecto no se elimina; siempre debe existir una base.
        if (config.getEmpleado() == null && config.getServicio() == null) {
            throw new RuntimeException("No se puede eliminar el valor por defecto. Edítalo en su lugar.");
        }
        arrendamientoRepository.delete(config);
    }

    private void validarValor(BigDecimal valor) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("El valor de arrendamiento debe ser mayor o igual a 0.");
        }
    }
}
