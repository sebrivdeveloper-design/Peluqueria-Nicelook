package co.edu.univalle.NiceLook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import co.edu.univalle.NiceLook.model.Empleado;

@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Integer> {
    Optional<Empleado> findByUsuario_Documento(String documento);
    List<Empleado> findByUsuarioRolNombreRol(String nombreRol);
}
