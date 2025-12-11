package com.example.eureka.general.port.out;

import com.example.eureka.metricas.domain.MetricasBasicas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ITiposMetricasRepository extends JpaRepository<MetricasBasicas,Integer> {

}
