package com.example.eureka.metricas.infrastructure.controller;

import com.example.eureka.metricas.infrastructure.dto.MetricaPreguntaDTO;
import com.example.eureka.metricas.infrastructure.dto.MetricasGeneralesDTO;
import com.example.eureka.metricas.infrastructure.dto.RankingGlobalDTO;
import com.example.eureka.metricas.infrastructure.dto.RankingPreguntaDTO;
import com.example.eureka.metricas.port.in.MetricasGeneralesService;
import com.example.eureka.metricas.port.in.MetricasPreguntaService;
import com.example.eureka.shared.util.PageResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping("/v1/metricas-generales")
@RequiredArgsConstructor
public class MetricasGeneralesController {


    private final MetricasGeneralesService metricasGeneralesService;
    private final MetricasPreguntaService metricasPreguntaService;

    @GetMapping("/emprendimiento/mayor-vista")
    public ResponseEntity<MetricasGeneralesDTO> mayorVista() {
        return ResponseEntity.ok(metricasGeneralesService.findTopByOrderByVistasDesc());
    }

    @GetMapping("/emprendimiento/menor-vista")
    public ResponseEntity<MetricasGeneralesDTO> menorVista() {
        return ResponseEntity.ok(metricasGeneralesService.findTopByOrderByVistasAsc());
    }

    @GetMapping("/categoria/mayor-vista")
    public ResponseEntity<HashMap<String, Object>> mayorVistaCategoria() {
        return ResponseEntity.ok(metricasGeneralesService.findTopByOrderByVistasCategoriaDesc());
    }

    @GetMapping("/emprendimiento/mayor-valoracion")
    public ResponseEntity<MetricaPreguntaDTO> mayorValoracion() {
        return ResponseEntity.ok(metricasGeneralesService.findTopByOrderByNivelValoracionDesc());
    }

    @GetMapping("/emprendimiento/menor-valoracion")
    public ResponseEntity<MetricaPreguntaDTO> menorValoracion() {
        return ResponseEntity.ok(metricasGeneralesService.findTopByOrderByNivelValoracionAsc());
    }

    @GetMapping("/valoracion")
    public ResponseEntity<PageResponseDTO<RankingGlobalDTO>> rankingGlobal(
            @org.springdoc.core.annotations.ParameterObject Pageable pageable) {

        Page<RankingGlobalDTO> page = metricasPreguntaService.obtenerRankingGlobal(pageable);
        return ResponseEntity.ok(PageResponseDTO.fromPage(page));
    }

    // Ranking por pregunta (opcional: filtrar por tipo de emprendimiento)
    @GetMapping("/pregunta/{idPregunta}")
    public ResponseEntity<PageResponseDTO<RankingPreguntaDTO>> rankingPorPregunta(
            @PathVariable Long idPregunta,
            @RequestParam(required = false) Long idTipoEmprendimiento,
            @org.springdoc.core.annotations.ParameterObject Pageable pageable) {

        Page<RankingPreguntaDTO> page =
                metricasPreguntaService.obtenerRankingPorPregunta(idPregunta, idTipoEmprendimiento, pageable);

        return ResponseEntity.ok(PageResponseDTO.fromPage(page));
    }

}
