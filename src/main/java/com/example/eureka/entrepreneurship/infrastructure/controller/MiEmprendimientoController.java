package com.example.eureka.entrepreneurship.infrastructure.controller;

import com.example.eureka.auth.domain.Usuarios;
import com.example.eureka.entrepreneurship.infrastructure.dto.shared.EmprendimientoResponseDTO;
import com.example.eureka.entrepreneurship.infrastructure.dto.shared.VistaEmprendedorDTO;
import com.example.eureka.entrepreneurship.port.out.IEmprendimientosRepository;
import com.example.eureka.entrepreneurship.port.in.EmprendimientoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/v1/mi-emprendimiento")
@RequiredArgsConstructor
public class MiEmprendimientoController {

    private final EmprendimientoService emprendimientoService;
    private final IEmprendimientosRepository emprendimientosRepository;

    /**
     * Obtener todos los emprendimientos de un usuario autenticado
     */
    @GetMapping("/mis-emprendimientos")
    public ResponseEntity<?> obtenerEmprendimientosPorUsuario(@AuthenticationPrincipal Usuarios usuario) {
        try {
            List<EmprendimientoResponseDTO> emprendimientos = emprendimientoService.obtenerEmprendimientosPorUsuario(usuario);
            return ResponseEntity.ok(emprendimientos);
        } catch (Exception e) {
            log.error("Error al obtener emprendimientos del usuario: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Obtener mi emprendimiento (emprendedor - incluye datos pendientes si existen)
     */
    @GetMapping("/{id}/mi-emprendimiento")
    public ResponseEntity<?> obtenerMiEmprendimiento(
            @PathVariable Integer id,
            @AuthenticationPrincipal Usuarios usuario) {
        try {
            var emprendimientoOpt = emprendimientosRepository.findById(id);
            if (emprendimientoOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Emprendimiento no encontrado"));
            }
            var emprendimiento = emprendimientoOpt.get();
            if (!emprendimiento.getUsuarios().getId().equals(usuario.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "No tienes acceso a este emprendimiento"));
            }
            VistaEmprendedorDTO vista = emprendimientoService.obtenerVistaEmprendedor(id);
            return ResponseEntity.ok(vista);
        } catch (Exception e) {
            log.error("Error al obtener emprendimiento: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
