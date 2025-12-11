package com.example.eureka.entrepreneurship.infrastructure.controller;

import com.example.eureka.auth.domain.Usuarios;
import com.example.eureka.entrepreneurship.infrastructure.dto.publico.EmprendimientoListaPublicoDTO;
import com.example.eureka.auth.aplication.services.UsuariosServiceImpl;
import com.example.eureka.entrepreneurship.infrastructure.dto.shared.EmprendimientoPorCategoriaDTO;
import com.example.eureka.entrepreneurship.infrastructure.dto.request.EmprendimientoRequestDTO;
import com.example.eureka.entrepreneurship.infrastructure.dto.shared.EmprendimientoResponseDTO;
import com.example.eureka.entrepreneurship.port.in.EmprendimientoService;
import com.example.eureka.entrepreneurship.domain.model.SolicitudAprobacion;
import com.example.eureka.shared.util.PageResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/v1/emprendimientos")
@RequiredArgsConstructor
public class EmprendimientoController {

    private final EmprendimientoService emprendimientoService;
    private final UsuariosServiceImpl usuariosServiceImpl;

    /**
     * Obtener emprendimiento por ID (público - solo datos publicados)
     */
    @GetMapping("/{id}/publico")
    public ResponseEntity<?> obtenerEmprendimientoPublico(@PathVariable Integer id) {
        try {
            EmprendimientoResponseDTO emprendimiento = emprendimientoService
                    .obtenerEmprendimientoCompletoPorId(id);
            return ResponseEntity.ok(emprendimiento);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Emprendimiento no encontrado"));
        }
    }

    /**
     * Obtener todos los emprendimientos por nombre
     */
    @GetMapping("/lista")
    public ResponseEntity<?> obtenerListaDeEmprendimientos(Authentication authentication) {
        try {
            // Validar autenticación
            if (authentication == null || authentication.getName() == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Usuario no autenticado"));
            }

            // Obtener usuario desde el token
            String email = authentication.getName();
            Usuarios usuario = usuariosServiceImpl.findByEmail(email);

            List<EmprendimientoListaPublicoDTO> emprendimientos =
                    emprendimientoService.obtenesListaDeEmprendimientos(usuario);
            return ResponseEntity.ok(emprendimientos);
        } catch (Exception e) {
            log.error("Error al obtener lista de emprendimientos: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Obtener todos los emprendimientos con filtros opcionales
     */
    @GetMapping()
    public ResponseEntity<PageResponseDTO<EmprendimientoResponseDTO>> obtenerEmprendimientosFiltrado(
            @RequestParam(value = "nombre", required = false) String nombre,
            @RequestParam(value = "tipo", required = false) String tipo,
            @RequestParam(value = "categoria", required = false) String categoria,
            @RequestParam(value = "ciudad", required = false) String ciudad,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        var pageable = PageRequest.of(page, size);
        var pageResult = emprendimientoService.obtenerEmprendimientosFiltrado(nombre, tipo, categoria, ciudad, pageable);
        return ResponseEntity.ok(PageResponseDTO.fromPage(pageResult));
    }

    /**
     * Obtener emprendimientos por categoría
     */
    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<?> obtenerPorCategoria(@PathVariable Integer categoriaId) {
        try {
            EmprendimientoPorCategoriaDTO resultado = emprendimientoService
                    .obtenerEmprendimientosPorCategoria(categoriaId);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            log.error("Error al obtener emprendimientos por categoría: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Crear estructura de emprendimiento (BORRADOR o enviar directamente)
     */
    @PostMapping()
    public ResponseEntity<?> crearEmprendimiento(
            @RequestBody EmprendimientoRequestDTO request) {
        try {
            Integer id = emprendimientoService.estructuraEmprendimiento(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            "mensaje", "Emprendimiento creado exitosamente",
                            "id", id
                    ));
        } catch (Exception e) {
            log.error("Error al crear emprendimiento: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Actualizar emprendimiento (guarda cambios en borrador o crea solicitud)
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarEmprendimiento(
            @PathVariable Integer id,
            @RequestBody EmprendimientoRequestDTO request) {
        try {
            EmprendimientoResponseDTO actualizado = emprendimientoService
                    .actualizarEmprendimiento(id, request);
            return ResponseEntity.ok(Map.of(
                    "mensaje", "Emprendimiento actualizado exitosamente",
                    "emprendimiento", actualizado
            ));
        } catch (Exception e) {
            log.error("Error al actualizar emprendimiento: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }


    /**
     * Enviar emprendimiento para aprobación
     */
    @PostMapping("/{id}/enviar-aprobacion")
    public ResponseEntity<?> enviarParaAprobacion(
            @PathVariable Integer id,
            @AuthenticationPrincipal Usuarios usuario) {
        try {
            SolicitudAprobacion solicitud = emprendimientoService.enviarParaAprobacion(id, usuario);
            return ResponseEntity.ok(Map.of(
                    "mensaje", "Emprendimiento enviado para aprobación",
                    "solicitudId", solicitud.getId(),
                    "estado", solicitud.getEstadoSolicitud()
            ));
        } catch (Exception e) {
            log.error("Error al enviar para aprobación: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/activar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> activarEmprendimiento(@PathVariable Integer id) throws Exception {
        emprendimientoService.activarEmprendimiento(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> eliminarEmprendimiento(@PathVariable Integer id) throws Exception {
        emprendimientoService.inactivarEmprendimiento(id);
        return ResponseEntity.ok().build();
    }
}