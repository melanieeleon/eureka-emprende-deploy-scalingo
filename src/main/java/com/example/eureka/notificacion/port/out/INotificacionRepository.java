package com.example.eureka.notificacion.port.out;

import com.example.eureka.entrepreneurship.domain.model.Notificacion;
import com.example.eureka.auth.domain.Usuarios;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface INotificacionRepository extends JpaRepository<Notificacion, Integer> {

    // Obtener notificaciones de un usuario con paginación
    Page<Notificacion> findByUsuarioOrderByFechaCreacionDesc(Usuarios usuario, Pageable pageable);

    // Obtener notificaciones no leídas
    List<Notificacion> findByUsuarioAndLeidaOrderByFechaCreacionDesc(Usuarios usuario, Boolean leida);

    // Contar no leídas
    Integer countByUsuarioAndLeida(Usuarios usuario, Boolean leida);

    // Marcar como leída
    @Modifying
    @Query("UPDATE Notificacion n SET n.leida = true, n.fechaLectura = :fecha WHERE n.id = :id")
    void marcarComoLeida(@Param("id") Integer id, @Param("fecha") LocalDateTime fecha);

    // Marcar todas como leídas
    @Modifying
    @Query("UPDATE Notificacion n SET n.leida = true, n.fechaLectura = :fecha " +
            "WHERE n.usuario = :usuario AND n.leida = false")
    void marcarTodasComoLeidas(@Param("usuario") Usuarios usuario, @Param("fecha") LocalDateTime fecha);

    // Eliminar notificación
    void deleteById(Integer id);

    // Eliminar notificaciones antiguas y leídas (limpieza automática)
    @Modifying
    @Query("DELETE FROM Notificacion n WHERE n.fechaCreacion < :fecha AND n.leida = true")
    void eliminarAntiguasLeidas(@Param("fecha") LocalDateTime fecha);

    // Obtener últimas N notificaciones
    List<Notificacion> findTop10ByUsuarioOrderByFechaCreacionDesc(Usuarios usuario);
}