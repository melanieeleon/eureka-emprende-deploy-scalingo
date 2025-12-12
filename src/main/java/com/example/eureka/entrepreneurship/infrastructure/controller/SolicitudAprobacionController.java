package com.example.eureka.entrepreneurship.infrastructure.controller;

import com.example.eureka.entrepreneurship.infrastructure.dto.shared.EmprendimientoCompletoDTO;
import com.example.eureka.entrepreneurship.infrastructure.dto.shared.SolicitudAprobacionDTO;
import com.example.eureka.entrepreneurship.infrastructure.dto.shared.VistaEmprendedorDTO;
import com.example.eureka.entrepreneurship.aplication.service.SolicitudAprobacionService;
import com.example.eureka.auth.domain.Usuarios;
import com.example.eureka.shared.util.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/solicitudes")
@RequiredArgsConstructor
public class SolicitudAprobacionController {

    private final SolicitudAprobacionService solicitudService;

    // ============= ENDPOINTS EMPRENDEDOR =============

    /**
     * Obtener vista del emprendedor (datos actuales + pendientes si existen)
     */
    @GetMapping("/emprendimiento/{id}/mi-vista")
    public ResponseEntity<VistaEmprendedorDTO> obtenerMiVista(
            @PathVariable Integer id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Usuarios usr = userDetails.getUsuario();
        log.info("Obteniendo vista para emprendimiento: {}", id);
        VistaEmprendedorDTO vista = solicitudService.obtenerVistaEmprendedor(id);
        return ResponseEntity.ok(vista);
    }

    /**
     * Enviar emprendimiento para aprobación
     */
    @PostMapping("/emprendimiento/{id}/enviar")
    public ResponseEntity<?> enviarParaAprobacion(
            @PathVariable Integer id,
            @RequestBody EmprendimientoCompletoDTO datosCompletos,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Usuarios usuario = userDetails.getUsuario();

        try {
            log.info("Enviando solicitud de emprendimiento: {}", id);
            var solicitud = solicitudService.crearSolicitud(id, datosCompletos, usuario);

            return ResponseEntity.ok(Map.of(
                    "mensaje", "Solicitud enviada correctamente",
                    "solicitudId", solicitud.getId(),
                    "estado", solicitud.getEstadoSolicitud()
            ));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error al enviar solicitud: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al procesar solicitud"));
        }
    }

    /**
     * Modificar y reenviar solicitud con observaciones
     */
    @PutMapping("/{solicitudId}/modificar-reenviar")
    public ResponseEntity<?> modificarYReenviar(
            @PathVariable Integer solicitudId,
            @RequestBody EmprendimientoCompletoDTO datosActualizados,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Usuarios usuario = userDetails.getUsuario();
        try {
            log.info("Modificando solicitud: {}", solicitudId);
            solicitudService.modificarYReenviar(solicitudId, datosActualizados, usuario);

            return ResponseEntity.ok(Map.of(
                    "mensaje", "Solicitud modificada y reenviada correctamente"
            ));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error al modificar solicitud: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al procesar solicitud"));
        }
    }

// ============= ENDPOINTS ADMIN =============

    /**
     * Listar todas las solicitudes pendientes
     */
    @GetMapping("/admin/pendientes")
    public ResponseEntity<List<SolicitudAprobacionDTO>> listarSolicitudesPendientes() {
        log.info("Listando solicitudes pendientes");
        List<SolicitudAprobacionDTO> solicitudes = solicitudService.listarSolicitudesPendientes();
        return ResponseEntity.ok(solicitudes);
    }

    /**
     * Obtener detalle de solicitud con comparación
     */
    @GetMapping("/admin/{solicitudId}/detalle")
    public ResponseEntity<Map<String, Object>> obtenerDetalleSolicitud(
            @PathVariable Integer solicitudId) {

        log.info("Obteniendo detalle de solicitud: {}", solicitudId);
        Map<String, Object> detalle = solicitudService.obtenerDetalleConComparacion(solicitudId);
        return ResponseEntity.ok(detalle);
    }

    /**
     * Obtener historial de una solicitud
     */
    @GetMapping("/admin/{solicitudId}/historial")
    public ResponseEntity<List<Map<String, Object>>> obtenerHistorial(
            @PathVariable Integer solicitudId) {

        log.info("Obteniendo historial de solicitud: {}", solicitudId);
        List<Map<String, Object>> historial = solicitudService.obtenerHistorial(solicitudId);
        return ResponseEntity.ok(historial);
    }

    /**
     * Aprobar solicitud
     */
    @PostMapping("/admin/{solicitudId}/aprobar")
    public ResponseEntity<?> aprobarSolicitud(
            @PathVariable Integer solicitudId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Usuarios admin = userDetails.getUsuario();
        try {
            log.info("Aprobando solicitud: {}", solicitudId);
            solicitudService.aprobarSolicitud(solicitudId, admin);

            return ResponseEntity.ok(Map.of(
                    "mensaje", "Solicitud aprobada correctamente",
                    "solicitudId", solicitudId
            ));
        } catch (Exception e) {
            log.error("Error al aprobar solicitud: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al aprobar solicitud: " + e.getMessage()));
        }
    }

    /**
     * Rechazar solicitud
     */
    @PostMapping("/admin/{solicitudId}/rechazar")
    public ResponseEntity<?> rechazarSolicitud(
            @PathVariable Integer solicitudId,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Usuarios admin = userDetails.getUsuario();
        String motivo = body.get("motivo");
        if (motivo == null || motivo.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Debe proporcionar un motivo de rechazo"));
        }

        try {
            log.info("Rechazando solicitud: {}", solicitudId);
            solicitudService.rechazarSolicitud(solicitudId, motivo, admin);

            return ResponseEntity.ok(Map.of(
                    "mensaje", "Solicitud rechazada correctamente",
                    "solicitudId", solicitudId
            ));
        } catch (Exception e) {
            log.error("Error al rechazar solicitud: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al rechazar solicitud: " + e.getMessage()));
        }
    }

    /**
     * Enviar observaciones para modificación
     */
    @PostMapping("/admin/{solicitudId}/observaciones")
    public ResponseEntity<?> enviarObservaciones(
            @PathVariable Integer solicitudId,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Usuarios admin = userDetails.getUsuario();

        String observaciones = body.get("observaciones");
        if (observaciones == null || observaciones.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Debe proporcionar observaciones"));
        }

        try {
            log.info("Enviando observaciones para solicitud: {}", solicitudId);
            solicitudService.enviarObservaciones(solicitudId, observaciones, admin);

            return ResponseEntity.ok(Map.of(
                    "mensaje", "Observaciones enviadas correctamente",
                    "solicitudId", solicitudId
            ));
        } catch (Exception e) {
            log.error("Error al enviar observaciones: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al enviar observaciones: " + e.getMessage()));
        }
    }
}