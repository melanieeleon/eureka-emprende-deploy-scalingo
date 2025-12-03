package com.example.eureka.metricas.controller;

import com.example.eureka.domain.model.EmprendimientoMetricas;
import com.example.eureka.domain.model.Emprendimientos;
import com.example.eureka.entrepreneurship.service.EmprendimientoService;
import com.example.eureka.metricas.dto.EmprendimientoMetricaDTO;
import com.example.eureka.metricas.service.EmprendimientoMetricasService;
import com.example.eureka.metricas.service.MetricasBasicasService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public ResponseEntity<List<EmprendimientoMetricas>> obtenerMetricasCantidad(){
        return ResponseEntity.ok(emprendimientoMetricasService.getEmprendimientosVisualizacion());
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
