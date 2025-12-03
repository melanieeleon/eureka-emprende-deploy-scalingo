package com.example.eureka.metricas.service;

import com.example.eureka.domain.model.EmprendimientoMetricas;
import com.example.eureka.domain.model.Emprendimientos;
import com.example.eureka.metricas.dto.EmprendimientoMetricaDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface EmprendimientoMetricasService {

    EmprendimientoMetricas save (EmprendimientoMetricaDTO emprendimientoMetricaDTO);
    EmprendimientoMetricas update(EmprendimientoMetricas emprendimientoMetricas);
    EmprendimientoMetricas findById(Long id);
    List<EmprendimientoMetricas> findAll();
    void delete(Long id);
    List<EmprendimientoMetricas> getEmprendimientosVisualizacion();
    List<EmprendimientoMetricas> getEmprendimientosFiltros(LocalDateTime fechaInicio, LocalDateTime fechaFin, Integer idEmprendimientos);

}
