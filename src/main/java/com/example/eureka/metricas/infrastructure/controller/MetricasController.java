package com.example.eureka.metricas.infrastructure.controller;

import com.example.eureka.entrepreneurship.domain.model.EmprendimientoMetricas;
import com.example.eureka.entrepreneurship.port.in.EmprendimientoService;
import com.example.eureka.metricas.infrastructure.dto.EmprendimientoMetricaDTO;
import com.example.eureka.metricas.port.in.EmprendimientoMetricasService;
import com.example.eureka.metricas.port.in.MetricasBasicasService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/metricas")
@RequiredArgsConstructor
public class MetricasController {

    private final MetricasBasicasService metricasBasicasService;
    private final EmprendimientoMetricasService emprendimientoMetricasService;
    private final EmprendimientoService emprendimientoService;

    @GetMapping("/cantidad-visualizacion")
    public ResponseEntity<Page<EmprendimientoMetricas>> obtenerMetricasCantidad(@org.springdoc.core.annotations.ParameterObject Pageable pageable){
        return ResponseEntity.ok(emprendimientoMetricasService.getEmprendimientosVisualizacion(pageable));
    }

    @GetMapping("/filtros/{fechaInicio}/{fechaFin}/{idEmprendimiento}")
    public ResponseEntity<List<EmprendimientoMetricas>> obtenerMetricasFiltros(@PathVariable LocalDateTime fechaInicio, @PathVariable LocalDateTime fechaFin, @PathVariable Integer idEmprendimiento){
        return ResponseEntity.ok(emprendimientoMetricasService.getEmprendimientosFiltros(fechaInicio, fechaFin, idEmprendimiento));
    }

    @PutMapping("/update")
    public ResponseEntity<EmprendimientoMetricas> actualizarMetricas(@RequestBody EmprendimientoMetricas emprendimientoMetricas){
        return ResponseEntity.ok(emprendimientoMetricasService.update(emprendimientoMetricas));
    }

    @PostMapping("/save")
    public ResponseEntity<EmprendimientoMetricas> guardarMetricas(@RequestBody EmprendimientoMetricaDTO emprendimientoMetricas){
        return ResponseEntity.ok(emprendimientoMetricasService.save(emprendimientoMetricas));

    }



}
