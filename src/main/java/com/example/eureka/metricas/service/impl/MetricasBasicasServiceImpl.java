package com.example.eureka.metricas.service.impl;


import com.example.eureka.domain.model.MetricasBasicas;
import com.example.eureka.metricas.repository.IMetricasBasicasRepository;
import com.example.eureka.metricas.service.MetricasBasicasService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MetricasBasicasServiceImpl implements MetricasBasicasService {

    private final IMetricasBasicasRepository metricasBasicasRepository;


    @Override
    public MetricasBasicas save(MetricasBasicas metricasBasicas) {
        return metricasBasicasRepository.save(metricasBasicas);
    }

    @Override
    public List<MetricasBasicas> findAll() {
        return metricasBasicasRepository.findAll();
    }

    @Override
    public MetricasBasicas findById(Integer id) {
        return metricasBasicasRepository.findById(id).get();
    }

    @Override
    public void deleteById(Integer id) {
        metricasBasicasRepository.deleteById(id);
    }
}
