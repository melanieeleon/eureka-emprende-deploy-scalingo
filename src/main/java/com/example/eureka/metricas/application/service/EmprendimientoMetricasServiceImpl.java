package com.example.eureka.metricas.application.service;


import com.example.eureka.entrepreneurship.domain.model.EmprendimientoMetricas;
import com.example.eureka.entrepreneurship.domain.model.Emprendimientos;
import com.example.eureka.metricas.domain.MetricasBasicas;
import com.example.eureka.entrepreneurship.port.out.IEmprendimientosRepository;
import com.example.eureka.metricas.infrastructure.dto.EmprendimientoMetricaDTO;
import com.example.eureka.metricas.port.out.IEmprendimientoMetricasRepository;
import com.example.eureka.metricas.port.out.IMetricasBasicasRepository;
import com.example.eureka.metricas.port.in.EmprendimientoMetricasService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmprendimientoMetricasServiceImpl implements EmprendimientoMetricasService {

    private final IEmprendimientoMetricasRepository emprendimientoMetricasRepository;
    private final IEmprendimientosRepository emprendimientosRepository;
    private final IMetricasBasicasRepository metricasBasicasRepository;


    @Override
    public EmprendimientoMetricas save(EmprendimientoMetricaDTO emprendimientoMetricaDTO) {
        Emprendimientos emp = emprendimientosRepository.findById(emprendimientoMetricaDTO.getIdEmprendimiento()).orElse(null);
        MetricasBasicas meb = metricasBasicasRepository.findById(emprendimientoMetricaDTO.getIdMetricaBasica()).orElse(null);
        EmprendimientoMetricas emprendimientoMetricas = new EmprendimientoMetricas();
        emprendimientoMetricas.setEmprendimiento(emp);
        emprendimientoMetricas.setMetrica(meb);
        emprendimientoMetricas.setValor(emprendimientoMetricaDTO.getValor());
        return emprendimientoMetricasRepository.save(emprendimientoMetricas);
    }

    @Override
    public EmprendimientoMetricas update(EmprendimientoMetricas emprendimientoMetricas) {
        return emprendimientoMetricasRepository.save(emprendimientoMetricas);
    }

    @Override
    public EmprendimientoMetricas findById(Long id) {
        return emprendimientoMetricasRepository.findById(id.intValue()).orElse(null);
    }

    @Override
    public List<EmprendimientoMetricas> findAll() {
        return emprendimientoMetricasRepository.findAll();
    }

    @Override
    public void delete(Long id) {
        emprendimientoMetricasRepository.deleteById(id.intValue());
    }

    @Override
    public Page<EmprendimientoMetricas> getEmprendimientosVisualizacion(Pageable pageable) {
        List<EmprendimientoMetricas> emprendimientoMetricas = emprendimientoMetricasRepository.findAll();
        List<EmprendimientoMetricas> empFin = emprendimientoMetricas.stream().filter(item -> esNumerico(item.getValor())).collect(Collectors.toList());
        return  new PageImpl<>(empFin, pageable, empFin.size());

    }

    @Override
    public List<EmprendimientoMetricas> getEmprendimientosFiltros(LocalDateTime fechaInicio, LocalDateTime fechaFin, Integer idEmprendimientos) {
        Emprendimientos emp = emprendimientosRepository.findByFechaCreacionIsBetweenAndId(fechaInicio, fechaFin, idEmprendimientos);
        return emprendimientoMetricasRepository.findByEmprendimiento(emp);
    }


    private boolean esNumerico(String valor) {
        return valor != null && valor.matches("\\d+");
    }

}
