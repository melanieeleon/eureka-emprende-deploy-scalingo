package com.example.eureka.metricas.port.out;

import com.example.eureka.metricas.domain.MetricasBasicas;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IMetricasBasicasRepository extends JpaRepository<MetricasBasicas, Integer> {
}
