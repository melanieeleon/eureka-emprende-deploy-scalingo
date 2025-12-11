package com.example.eureka.notificacion.port.out;

import com.example.eureka.notificacion.domain.TipoNotificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ITipoNotificacionRepository extends JpaRepository<TipoNotificacion, Long> {

    Optional<TipoNotificacion> findByCodigo(String codigo);

    Optional<TipoNotificacion> findByCodigoAndActivoTrue(String codigo);
}