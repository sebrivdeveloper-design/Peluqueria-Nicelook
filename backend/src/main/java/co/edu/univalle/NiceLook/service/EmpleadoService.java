package co.edu.univalle.NiceLook.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import co.edu.univalle.NiceLook.DTO.EmpleadoRequest;
import co.edu.univalle.NiceLook.model.*;
import co.edu.univalle.NiceLook.repository.*;

@Service
public class EmpleadoService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private RolRepository rolRepository;

    public Empleado crearEmpleado(EmpleadoRequest request) {

        if (usuarioRepository.findByCorreo(request.getCorreo()).isPresent()) {
            throw new RuntimeException("El usuario ya existe");
        }

        Rol rol = rolRepository.findByNombreRol(request.getRol())
                .orElseThrow(() -> new RuntimeException("Rol no válido"));

        Usuario usuario = new Usuario();
        usuario.setNombreCompleto(request.getNombreCompleto());
        usuario.setCorreo(request.getCorreo());
        usuario.setTelefono(request.getTelefono());
        usuario.setDocumento(request.getDocumento());
        usuario.setRol(rol);
        usuario.setFechaRegistro(LocalDateTime.now());
        usuario.setEstado("activo");
        usuario.setEsGoogleUser(true);
        usuarioRepository.save(usuario);

        Empleado empleado = new Empleado();
        empleado.setUsuario(usuario);
        empleado.setDocumento(request.getDocumento());
        empleado.setEspecialidad(request.getEspecialidad());
        empleado.setSalario(request.getSalario());
        empleado.setFechaIngreso(LocalDate.now());
        empleado.setEstadoLaboral("activo");

        return empleadoRepository.save(empleado);
    }

    public Empleado editarEmpleado(Integer id, EmpleadoRequest request) {

        Empleado empleado = empleadoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));

        Usuario usuario = empleado.getUsuario();
        usuario.setNombreCompleto(request.getNombreCompleto());
        usuario.setTelefono(request.getTelefono());
        usuario.setDocumento(request.getDocumento());

        // Solo actualizar correo si cambió
        if (!usuario.getCorreo().equals(request.getCorreo())) {
            if (usuarioRepository.findByCorreo(request.getCorreo()).isPresent()) {
                throw new RuntimeException("Ya existe un empleado con ese correo");
            }
            usuario.setCorreo(request.getCorreo());
        }

        // Actualizar rol si se envió
        if (request.getRol() != null && !request.getRol().isEmpty()) {
            Rol rol = rolRepository.findByNombreRol(request.getRol())
                    .orElseThrow(() -> new RuntimeException("Rol no válido"));
            usuario.setRol(rol);
        }

        usuarioRepository.save(usuario);

        empleado.setEspecialidad(request.getEspecialidad());
        empleado.setSalario(request.getSalario());
        empleado.setDocumento(request.getDocumento());

        return empleadoRepository.save(empleado);
    }

    public List<Empleado> listar() {
        return empleadoRepository.findAll();
    }
}
