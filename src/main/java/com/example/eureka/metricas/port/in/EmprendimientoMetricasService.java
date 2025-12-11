package com.example.eureka.metricas.port.in;

import com.example.eureka.entrepreneurship.domain.model.EmprendimientoMetricas;
import com.example.eureka.metricas.infrastructure.dto.EmprendimientoMetricaDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface EmprendimientoMetricasService {

    EmprendimientoMetricas save (EmprendimientoMetricaDTO emprendimientoMetricaDTO);
    EmprendimientoMetricas update(EmprendimientoMetricas emprendimientoMetricas);
    EmprendimientoMetricas findById(Long id);
    List<EmprendimientoMetricas> findAll();
    void delete(Long id);
    Page<EmprendimientoMetricas> getEmprendimientosVisualizacion(Pageable pageable);
    List<EmprendimientoMetricas> getEmprendimientosFiltros(LocalDateTime fechaInicio, LocalDateTime fechaFin, Integer idEmprendimientos);

}
