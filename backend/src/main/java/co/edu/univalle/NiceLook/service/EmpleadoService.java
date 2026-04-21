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

        // Validar usuario existente
        if (usuarioRepository.findByCorreo(request.getCorreo()).isPresent()) {
            throw new RuntimeException("El usuario ya existe");
        }

        // Buscar rol
        Rol rol = rolRepository.findByNombreRol(request.getRol())
                .orElseThrow(() -> new RuntimeException("Rol no válido"));

        // Crear usuario
        Usuario usuario = new Usuario();
        usuario.setNombreCompleto(request.getNombreCompleto());
        usuario.setCorreo(request.getCorreo());
        usuario.setTelefono(request.getTelefono());
        usuario.setRol(rol);
        usuario.setFechaRegistro(LocalDateTime.now());
        usuario.setEstado("activo");
        usuario.setEsGoogleUser(true);

        usuarioRepository.save(usuario);

        // Crear empleado
        Empleado empleado = new Empleado();
        empleado.setUsuario(usuario);
        empleado.setDocumento(request.getDocumento());
        empleado.setEspecialidad(request.getEspecialidad());
        empleado.setSalario(request.getSalario());
        empleado.setFechaIngreso(LocalDate.now());
        empleado.setEstadoLaboral("activo");

        return empleadoRepository.save(empleado);
    }

    public List<Empleado> listar() {
    return empleadoRepository.findAll();
    }

}
