package com.example.eureka.autoevaluacion.infrastructure.controller;

import com.example.eureka.autoevaluacion.infrastructure.dto.ListadoAutoevaluacionDTO;
import com.example.eureka.autoevaluacion.infrastructure.dto.RespuestaResponseDTO;
import com.example.eureka.autoevaluacion.port.in.AutoevaluacionService;
import com.example.eureka.autoevaluacion.domain.model.Respuesta;
import com.example.eureka.formulario.infrastructure.dto.response.OpcionRespuestaDTO;
import com.example.eureka.shared.util.PageResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Slf4j
@RestController
@RequestMapping("/v1/autoevaluacion")
@RequiredArgsConstructor
public class AutoevaluacionController {

    private final AutoevaluacionService autoevaluacionService;


    /**
     * Listar todas las autoevaluaciones con paginación
     * GET /api/autoevaluaciones?page=0&size=10&sort=fechaRespuesta,desc

    @GetMapping
    public ResponseEntity<Page<ListadoAutoevaluacionDTO>> listarAutoevaluaciones(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fechaRespuesta,desc") String sort) {

        Pageable pageable = org.springframework.data.domain.PageRequest.of(
                page,
                size,
                org.springframework.data.domain.Sort.by(sort)
        );

        Page<ListadoAutoevaluacionDTO> autoevaluaciones = autoevaluacionService.listarAutoevaluaciones(pageable);

        return ResponseEntity.ok(autoevaluaciones);
    }   */

    @GetMapping("/{idAutoevaluacion}/detalle")
    public ResponseEntity<PageResponseDTO<OpcionRespuestaDTO>> obtenerDetalleAutoevaluacion(
            @PathVariable Long idAutoevaluacion,
            @org.springdoc.core.annotations.ParameterObject Pageable pageable) {

        Page<OpcionRespuestaDTO> page = autoevaluacionService.obtenerDetalleAutoevaluacion(idAutoevaluacion, pageable);
        return ResponseEntity.ok(PageResponseDTO.fromPage(page));
    }

    @GetMapping
    public ResponseEntity<PageResponseDTO<ListadoAutoevaluacionDTO>> listarAutoevaluaciones(
            @org.springdoc.core.annotations.ParameterObject Pageable pageable) {

        Page<ListadoAutoevaluacionDTO> page = autoevaluacionService.listarAutoevaluaciones(pageable);
        return ResponseEntity.ok(PageResponseDTO.fromPage(page));
    }

    //NO SE PARA QUE SIRVE

    @PostMapping("/save")
    public ResponseEntity<Respuesta> saveRespuesta(@RequestBody RespuestaResponseDTO respuesta){
        return ResponseEntity.ok(autoevaluacionService.saveRespuesta(respuesta));
    }

}
