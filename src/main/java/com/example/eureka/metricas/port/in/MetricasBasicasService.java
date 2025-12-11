package com.example.eureka.metricas.port.in;

import com.example.eureka.metricas.domain.MetricasBasicas;

import java.util.List;

public interface MetricasBasicasService {

    MetricasBasicas save(MetricasBasicas metricasBasicas);
    List<MetricasBasicas> findAll();
    MetricasBasicas findById(Integer id);
    void deleteById(Integer id);
}
