package com.example.eureka.articulo.infrastructure.controller;

import com.example.eureka.articulo.infrastructure.dto.request.ArticuloRequestDTO;
import com.example.eureka.articulo.infrastructure.dto.request.TagRequestDTO;
import com.example.eureka.articulo.infrastructure.dto.response.ArticuloAdminDTO;
import com.example.eureka.articulo.infrastructure.dto.response.ArticuloPublicoDTO;
import com.example.eureka.articulo.infrastructure.dto.response.ArticuloResponseDTO;
import com.example.eureka.articulo.infrastructure.dto.response.TagDTO;
import com.example.eureka.articulo.aplication.service.ArticuloServiceImpl;
import com.example.eureka.articulo.infrastructure.specification.ValidationGroups;
import com.example.eureka.shared.enums.EstadoArticulo;
import com.example.eureka.shared.util.PageResponseDTO;
import com.example.eureka.shared.util.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/v1/blog")
@RequiredArgsConstructor
public class ArticuloController {

    private final ArticuloServiceImpl blogService;
    private final SecurityUtils securityUtils;

    // ========== Endpoints Públicos ==========

    @GetMapping("/publico/articulos")
    public ResponseEntity<PageResponseDTO<ArticuloPublicoDTO>> obtenerArticulosPublicos(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin,
            @RequestParam(required = false) Integer idTag,
            @RequestParam(required = false) String titulo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ArticuloPublicoDTO> articulos = blogService.obtenerArticulosPublicos(
                fechaInicio, fechaFin, idTag, titulo, pageable);

        // Convertir a formato personalizado
        PageResponseDTO<ArticuloPublicoDTO> response = PageResponseDTO.fromPage(articulos);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/publico/articulos/{idArticulo}")
    public ResponseEntity<ArticuloResponseDTO> obtenerArticuloPublicoPorId(
            @PathVariable Integer idArticulo) {
        ArticuloResponseDTO articulo = blogService.obtenerArticuloPublicoPorId(idArticulo);
        return ResponseEntity.ok(articulo);
    }

    @GetMapping("/tags")
    public ResponseEntity<?> obtenerTodosTags() {
        var tags = blogService.obtenerTodosTags();
        return ResponseEntity.ok(tags);
    }

    // ========== Endpoints Admin ==========

    @GetMapping("/admin/articulos")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<PageResponseDTO<ArticuloAdminDTO>> obtenerArticulosAdmin(
            @RequestParam(required = false) EstadoArticulo estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin,
            @RequestParam(required = false) Integer idTag,
            @RequestParam(required = false) String titulo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ArticuloAdminDTO> articulos = blogService.obtenerArticulosAdmin(
                estado, fechaInicio, fechaFin, idTag, titulo, pageable);

        // Convertir a formato personalizado
        PageResponseDTO<ArticuloAdminDTO> response = PageResponseDTO.fromPage(articulos);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin/articulos/{idArticulo}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ArticuloResponseDTO> obtenerArticuloPorId(
            @PathVariable Integer idArticulo) {
        ArticuloResponseDTO articulo = blogService.obtenerArticuloPorId(idArticulo);
        return ResponseEntity.ok(articulo);
    }

    @PostMapping("/articulos/crear")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ArticuloResponseDTO> crearArticulo(
            @Validated(ValidationGroups.OnCreate.class) @ModelAttribute ArticuloRequestDTO request) { // ⬅️ Cambiar aquí
        Integer idUsuario = securityUtils.getIdUsuarioAutenticado();
        ArticuloResponseDTO articulo = blogService.crearArticulo(request, idUsuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(articulo);
    }

    @PutMapping("/articulos/{idArticulo}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ArticuloResponseDTO> editarArticulo(
            @PathVariable Integer idArticulo,
            @Validated(ValidationGroups.OnUpdate.class) @ModelAttribute ArticuloRequestDTO request) { // ⬅️ Cambiar aquí
        Integer idUsuario = securityUtils.getIdUsuarioAutenticado();
        ArticuloResponseDTO articulo = blogService.editarArticulo(idArticulo, request, idUsuario);
        return ResponseEntity.ok(articulo);
    }

    @PutMapping("/articulos/{idArticulo}/archivar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<String> archivarArticulo(@PathVariable Integer idArticulo) {
        Integer idUsuario = securityUtils.getIdUsuarioAutenticado();
        blogService.archivarArticulo(idArticulo, idUsuario);
        return ResponseEntity.ok("Artículo archivado exitosamente");
    }

    @PutMapping("/articulos/{idArticulo}/desarchivar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<String> desarchivarArticulo(@PathVariable Integer idArticulo) {
        Integer idUsuario = securityUtils.getIdUsuarioAutenticado();
        blogService.desarchivarArticulo(idArticulo, idUsuario);
        return ResponseEntity.ok("Artículo desarchivado exitosamente");
    }

    @PostMapping("/tags/crear")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<TagDTO> crearTag(@Valid @RequestBody TagRequestDTO request) {
        Integer idUsuario = securityUtils.getIdUsuarioAutenticado();
        TagDTO tag = blogService.crearTag(request.getNombre(), idUsuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(tag);
    }
}