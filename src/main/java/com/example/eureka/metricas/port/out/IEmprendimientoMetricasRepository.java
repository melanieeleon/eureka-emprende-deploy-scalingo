package com.example.eureka.metricas.port.out;

import com.example.eureka.entrepreneurship.domain.model.EmprendimientoMetricas;
import com.example.eureka.entrepreneurship.domain.model.Emprendimientos;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IEmprendimientoMetricasRepository extends JpaRepository<EmprendimientoMetricas, Integer> {

    List<EmprendimientoMetricas> findByEmprendimiento(Emprendimientos emprendimiento);
}
