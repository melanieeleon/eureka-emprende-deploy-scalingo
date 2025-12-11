package com.example.eureka.notificacion.port.in;

import com.example.eureka.entrepreneurship.domain.model.Notificacion;
import com.example.eureka.notificacion.infrastructure.dto.NotificacionDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface NotificacionService {

    Notificacion crearNotificacion(
            Integer usuarioId,
            String codigoTipo,
            Map<String, Object> parametros,
            String enlace,
            Integer emprendimientoId,
            Integer solicitudId);

    Page<NotificacionDTO> obtenerNotificaciones(Integer usuarioId, Pageable pageable);

    List<NotificacionDTO> obtenerNoLeidas(Integer usuarioId);

    List<NotificacionDTO> obtenerUltimas(Integer usuarioId);

    Integer contarNoLeidas(Integer usuarioId);

    void marcarComoLeida(Integer notificacionId, Integer usuarioId);

    void marcarTodasComoLeidas(Integer usuarioId);

    void eliminar(Integer notificacionId, Integer usuarioId);

    void limpiarNotificacionesAntiguas(int diasAntiguedad);

    String procesarPlantilla(String plantilla, Map<String, Object> parametros);

    NotificacionDTO toDTO(Notificacion n);

}