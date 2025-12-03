package com.example.eureka.metricas.service;

import com.example.eureka.domain.model.MetricasBasicas;

import java.util.List;

public interface MetricasBasicasService {

    MetricasBasicas save(MetricasBasicas metricasBasicas);
    List<MetricasBasicas> findAll();
    MetricasBasicas findById(Integer id);
    void deleteById(Integer id);
}
