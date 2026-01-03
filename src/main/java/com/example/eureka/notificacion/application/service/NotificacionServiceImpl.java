package com.example.eureka.notificacion.application.service;


import com.example.eureka.auth.domain.Usuarios;
import com.example.eureka.auth.port.out.IUserRepository;
import com.example.eureka.entrepreneurship.domain.model.Notificacion;
import com.example.eureka.notificacion.domain.TipoNotificacion;
import com.example.eureka.entrepreneurship.port.out.IEmprendimientosRepository;
import com.example.eureka.entrepreneurship.port.out.ISolicitudAprobacionRepository;
import com.example.eureka.notificacion.infrastructure.dto.NotificacionDTO;
import com.example.eureka.notificacion.port.out.INotificacionRepository;
import com.example.eureka.notificacion.port.out.ITipoNotificacionRepository;
import com.example.eureka.notificacion.port.in.NotificacionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificacionServiceImpl implements NotificacionService {


    private final INotificacionRepository notificacionRepository;
    private final ITipoNotificacionRepository tipoNotificacionRepository;
    private final IUserRepository userRepository;
    private final IEmprendimientosRepository emprendimientoRepository;
    private final ISolicitudAprobacionRepository solicitudRepository;

    private Notificacion crearNotificacionBase(
            Integer usuarioId,
            String codigoTipo,
            String titulo,
            String mensaje,
            String enlace,
            Integer emprendimientoId,
            Integer solicitudId,
            Map<String, Object> metadata // opcional
    ) {

        Usuarios usuario = userRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        TipoNotificacion tipo = tipoNotificacionRepository
                .findByCodigoAndActivoTrue(codigoTipo)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Tipo de notificación no encontrado: " + codigoTipo));

        Notificacion notificacion = Notificacion.builder()
                .usuario(usuario)
                .tipoNotificacion(tipo)
                .titulo(titulo)
                .mensaje(mensaje)
                .enlace(enlace)
                .metadata(metadata)
                .prioridad(tipo.getPrioridad())
                .build();

        if (emprendimientoId != null) {
            emprendimientoRepository.findById(emprendimientoId)
                    .ifPresent(notificacion::setEmprendimiento);
        }

        if (solicitudId != null) {
            solicitudRepository.findById(solicitudId)
                    .ifPresent(notificacion::setSolicitud);
        }

        return notificacionRepository.save(notificacion);
    }

    @Transactional
    public Notificacion crearNotificacion(
            Integer usuarioId,
            String codigoTipo,
            Map<String, Object> parametros,
            String enlace,
            Integer emprendimientoId,
            Integer solicitudId) {

        TipoNotificacion tipo = tipoNotificacionRepository
                .findByCodigoAndActivoTrue(codigoTipo)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Tipo de notificación no encontrado: " + codigoTipo));

        String titulo = procesarPlantilla(tipo.getPlantillaTitulo(), parametros);
        String mensaje = procesarPlantilla(tipo.getPlantillaMensaje(), parametros);

        return crearNotificacionBase(
                usuarioId,
                codigoTipo,
                titulo,
                mensaje,
                enlace,
                emprendimientoId,
                solicitudId,
                parametros // metadata
        );
    }


    @Transactional
    public Notificacion crearNotificacionAutoevaluacion(
            Integer usuarioId,
            String codigoTipo,
            String titulo,
            String mensaje,
            String enlace,
            Integer emprendimientoId) {

        return crearNotificacionBase(
                usuarioId,
                codigoTipo,
                titulo,
                mensaje,
                enlace,
                emprendimientoId,
                null,      // sin solicitud
                null       // sin metadata (o la que quieras)
        );
    }


    @Override
    public NotificacionDTO obtenerNotificacionPorId(Integer id) {
        Optional<Notificacion> notificacion = notificacionRepository.findById(id);
        return notificacion.map(this::toDTO).orElse(null);
    }

    /**
     * Obtener notificaciones de un usuario (paginadas)
     */
    public Page<NotificacionDTO> obtenerNotificaciones(Integer usuarioId, Pageable pageable) {
        Usuarios usuario = userRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        Page<Notificacion> notificaciones = notificacionRepository
                .findByUsuarioOrderByFechaCreacionDesc(usuario, pageable);

        return notificaciones.map(this::toDTO);
    }

    /**
     * Obtener notificaciones no leídas
     */
    public List<NotificacionDTO> obtenerNoLeidas(Integer usuarioId) {
        Usuarios usuario = userRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        List<Notificacion> notificaciones = notificacionRepository
                .findByUsuarioAndLeidaOrderByFechaCreacionDesc(usuario, false);

        return notificaciones.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtener últimas 10 notificaciones
     */
    public List<NotificacionDTO> obtenerUltimas(Integer usuarioId) {
        Usuarios usuario = userRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        List<Notificacion> notificaciones = notificacionRepository
                .findTop10ByUsuarioOrderByFechaCreacionDesc(usuario);

        return notificaciones.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Contar no leídas
     */
    public Integer contarNoLeidas(Integer usuarioId) {
        Usuarios usuario = userRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        return notificacionRepository.countByUsuarioAndLeida(usuario, false);
    }

    /**
     * Marcar como leída
     */
    @Transactional
    public void marcarComoLeida(Integer notificacionId, Integer usuarioId) {
        // Verificar que la notificación pertenece al usuario
        Notificacion notificacion = notificacionRepository.findById(notificacionId)
                .orElseThrow(() -> new EntityNotFoundException("Notificación no encontrada"));

        if (!notificacion.getUsuario().getId().equals(usuarioId)) {
            throw new SecurityException("No tienes permiso para modificar esta notificación");
        }

        notificacionRepository.marcarComoLeida(notificacionId, LocalDateTime.now());
        log.debug("Notificación {} marcada como leída", notificacionId);
    }

    /**
     * Marcar todas como leídas
     */
    @Transactional
    public void marcarTodasComoLeidas(Integer usuarioId) {
        Usuarios usuario = userRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        notificacionRepository.marcarTodasComoLeidas(usuario, LocalDateTime.now());
        log.info("Todas las notificaciones del usuario {} marcadas como leídas", usuarioId);
    }

    /**
     * Eliminar notificación
     */
    @Transactional
    public void eliminar(Integer notificacionId, Integer usuarioId) {
        // Verificar que la notificación pertenece al usuario
        Notificacion notificacion = notificacionRepository.findById(notificacionId)
                .orElseThrow(() -> new EntityNotFoundException("Notificación no encontrada"));

        if (!notificacion.getUsuario().getId().equals(usuarioId)) {
            throw new SecurityException("No tienes permiso para eliminar esta notificación");
        }

        notificacionRepository.deleteById(notificacionId);
        log.debug("Notificación {} eliminada", notificacionId);
    }

    /**
     * Limpiar notificaciones antiguas (Task programada)
     */
    @Transactional
    public void limpiarNotificacionesAntiguas(int diasAntiguedad) {
        LocalDateTime fechaLimite = LocalDateTime.now().minusDays(diasAntiguedad);
        notificacionRepository.eliminarAntiguasLeidas(fechaLimite);
        log.info("Notificaciones antiguas eliminadas (anteriores a: {})", fechaLimite);
    }

    // ===== MÉTODOS AUXILIARES =====

    /**
     * Procesar plantilla: reemplazar {variable} con valores
     */
    public String procesarPlantilla(String plantilla, Map<String, Object> parametros) {
        if (parametros == null || parametros.isEmpty()) {
            return plantilla;
        }

        String resultado = plantilla;
        for (Map.Entry<String, Object> entry : parametros.entrySet()) {
            String placeholder = "{" + entry.getKey() + "}";
            String valor = entry.getValue() != null ? entry.getValue().toString() : "";
            resultado = resultado.replace(placeholder, valor);
        }

        return resultado;
    }

    /**
     * Convertir entidad a DTO
     */
    public NotificacionDTO toDTO(Notificacion n) {
        return NotificacionDTO.builder()
                .id(n.getId())
                .titulo(n.getTitulo())
                .mensaje(n.getMensaje())
                .enlace(n.getEnlace())
                .leida(n.getLeida())
                .fechaCreacion(n.getFechaCreacion())
                .fechaLectura(n.getFechaLectura())
                .prioridad(n.getPrioridad())
                .tipoNombre(n.getTipoNotificacion().getNombre())
                .icono(n.getTipoNotificacion().getIcono())
                .color(n.getTipoNotificacion().getColor())
                .metadata(n.getMetadata())
                .emprendimientoId(n.getEmprendimiento() != null ? n.getEmprendimiento().getId() : null)
                .nombreEmprendimiento(n.getEmprendimiento() != null ? n.getEmprendimiento().getNombreComercial() : null)
                .solicitudId(n.getSolicitud() != null ? Math.toIntExact(n.getSolicitud() != null ? n.getSolicitud().getId() : null) : null)
                .build();
    }
}
