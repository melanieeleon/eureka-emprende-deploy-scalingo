package com.example.eureka.entrepreneurship.port.out;

import com.example.eureka.entrepreneurship.domain.model.EmprendimientoMetricas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IEmprendimientoMetricaRepository extends JpaRepository<EmprendimientoMetricas,Integer> {
    List<EmprendimientoMetricas> findByEmprendimientoId(Integer emprendimientoId);
    List<EmprendimientoMetricas> findByEmprendimientoIdIn(List<Integer> emprendimientoIds);

}
