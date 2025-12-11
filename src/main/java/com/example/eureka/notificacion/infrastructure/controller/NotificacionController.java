package com.example.eureka.notificacion.infrastructure.controller;


import com.example.eureka.auth.aplication.services.UsuariosServiceImpl;
import com.example.eureka.entrepreneurship.domain.model.Notificacion;
import com.example.eureka.notificacion.infrastructure.dto.request.NotificacionRequestDTO;
import com.example.eureka.notificacion.port.in.NotificacionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/v1/notificacion")
@RequiredArgsConstructor
public class NotificacionController {

    private final NotificacionService notificacionService;

    private final UsuariosServiceImpl usuariosServiceImpl;

    @PostMapping()
    public ResponseEntity<?>  crearNotificacion(@RequestBody NotificacionRequestDTO  notificacionRequestDTO){
        try {

            Notificacion notificacion = notificacionService.crearNotificacion(notificacionRequestDTO.getUsuarioId(),
                    notificacionRequestDTO.getCodigoTipo(),
                    notificacionRequestDTO.getParametros(),
                    notificacionRequestDTO.getEnlace(),
                    notificacionRequestDTO.getEmprendimientoId(),
                    notificacionRequestDTO.getSolicitudId());


            ObjectMapper mapper = new ObjectMapper();
            String notificacionJson = mapper.writeValueAsString(notificacion);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            "mensaje", "Notificacion creada exitosamente",
                            "data", notificacionJson
                    ));
        } catch (Exception e) {
            log.error("Error al crear notificacion: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }


    @GetMapping()
    public ResponseEntity<?>  obtenerNotificacion(@RequestParam(value = "id") Integer id,
                                                  @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                  @RequestParam(value = "size", required = false, defaultValue = "10") int size){
        var pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(notificacionService.obtenerNotificaciones(id, pageable));
    }

    @GetMapping("/no-leidas")
    public ResponseEntity<?>  obtenerNoLeidas(@RequestParam(value = "usuarioId") Integer usuarioId){
        return ResponseEntity.ok(notificacionService.obtenerNoLeidas(usuarioId));
    }

    @GetMapping("/ultimas")
    public ResponseEntity<?>  obtenerUltimas(@RequestParam(value = "usuarioId") Integer usuarioId){
        return ResponseEntity.ok(notificacionService.obtenerUltimas(usuarioId));
    }


    @GetMapping("/contar-no-leidas")
    public ResponseEntity<?>  contarNoLeidas(@RequestParam(value = "usuarioId") Integer usuarioId){
        return ResponseEntity.ok(notificacionService.contarNoLeidas(usuarioId));
    }

    @PutMapping("/marcar-leida")
    public ResponseEntity<?>  marcarComoLeida(@RequestParam(value = "usuarioId") Integer usuarioId,
                                              @RequestParam(value = "notificacionId") Integer notificacionId){
        notificacionService.marcarComoLeida(notificacionId, usuarioId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of(
                        "mensaje", "Realizado exitosamente",
                        "estado", 200
                ));
    }


    @PutMapping("/marcar-todas-leidas")
    public ResponseEntity<?>  marcarTodasComoLeidas(@RequestParam(value = "usuarioId") Integer usuarioId){
        notificacionService.marcarTodasComoLeidas(usuarioId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of(
                        "mensaje", "Realizado exitosamente",
                        "estado", 200
                ));
    }

    @DeleteMapping()
    public ResponseEntity<?>  eliminar(@RequestParam(value = "usuarioId") Integer usuarioId,
                                              @RequestParam(value = "notificacionId") Integer notificacionId){
        notificacionService.eliminar(notificacionId, usuarioId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of(
                        "mensaje", "Eliminado exitosamente",
                        "estado", 200
                ));
    }

    @PutMapping("/limpiar-antiguas")
    public ResponseEntity<?>  limpiarNotificacionesAntiguas(@RequestParam(value = "diasAntiguedad") Integer dias){
        notificacionService.limpiarNotificacionesAntiguas(dias);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of(
                        "mensaje", "Realizado exitosamente",
                        "estado", 200
                ));
    }




}
