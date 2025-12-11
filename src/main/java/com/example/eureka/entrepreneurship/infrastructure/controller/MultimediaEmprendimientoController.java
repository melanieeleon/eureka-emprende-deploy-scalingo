package com.example.eureka.entrepreneurship.infrastructure.controller;

import com.example.eureka.entrepreneurship.port.in.MultimediaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/v1/emprendimientos/{emprendimientoId}/multimedia")
@RequiredArgsConstructor
public class MultimediaEmprendimientoController {

    private final MultimediaService multimediaService;

    /**
     * Agregar una imagen al emprendimiento
     */
    @PostMapping
    public ResponseEntity<?> agregarImagen(
            @PathVariable Integer emprendimientoId,
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam("tipo") String tipo) {
        try {
            multimediaService.agregarImagen(emprendimientoId, archivo, tipo);
            return ResponseEntity.ok(Map.of(
                    "mensaje", "Imagen agregada exitosamente"
            ));
        } catch (Exception e) {
            log.error("Error al agregar imagen: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Agregar múltiples imágenes al emprendimiento
     */
    @PostMapping("/multiple")
    public ResponseEntity<?> agregarImagenes(
            @PathVariable Integer emprendimientoId,
            @RequestParam("archivos") List<MultipartFile> archivos,
            @RequestParam("tipos") List<String> tipos) {
        try {
            multimediaService.agregarImagenes(emprendimientoId, archivos, tipos);
            return ResponseEntity.ok(Map.of(
                    "mensaje", archivos.size() + " imágenes agregadas exitosamente"
            ));
        } catch (Exception e) {
            log.error("Error al agregar imágenes: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Eliminar una imagen del emprendimiento
     */
    @DeleteMapping("/{multimediaId}")
    public ResponseEntity<?> eliminarImagen(
            @PathVariable Integer emprendimientoId,
            @PathVariable Long multimediaId) {
        try {
            multimediaService.eliminarImagen(emprendimientoId, multimediaId);
            return ResponseEntity.ok(Map.of(
                    "mensaje", "Imagen eliminada exitosamente"
            ));
        } catch (Exception e) {
            log.error("Error al eliminar imagen: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Obtener todas las imágenes de un emprendimiento
     */
    @GetMapping
    public ResponseEntity<?> obtenerImagenes(@PathVariable Integer emprendimientoId) {
        try {
            var multimedia = multimediaService.obtenerMultimedia(emprendimientoId);
            return ResponseEntity.ok(multimedia);
        } catch (Exception e) {
            log.error("Error al obtener imágenes: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Obtener imágenes por tipo (LOGO, PORTADA, GALERIA)
     */
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<?> obtenerImagenesPorTipo(
            @PathVariable Integer emprendimientoId,
            @PathVariable String tipo) {
        try {
            var multimedia = multimediaService.obtenerMultimediaPorTipo(emprendimientoId, tipo);
            return ResponseEntity.ok(multimedia);
        } catch (Exception e) {
            log.error("Error al obtener imágenes por tipo: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}