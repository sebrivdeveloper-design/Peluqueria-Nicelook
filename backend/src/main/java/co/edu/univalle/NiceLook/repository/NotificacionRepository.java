package co.edu.univalle.NiceLook.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.univalle.NiceLook.model.Notificacion;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Integer> {

    List<Notificacion> findByRolDestinoOrderByFechaDesc(String rolDestino);

    List<Notificacion> findByRolDestinoAndLeidaFalse(String rolDestino);
}
