package com.example.eureka.metricas.repository;

import com.example.eureka.domain.model.MetricasBasicas;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IMetricasBasicasRepository extends JpaRepository<MetricasBasicas, Integer> {
}
