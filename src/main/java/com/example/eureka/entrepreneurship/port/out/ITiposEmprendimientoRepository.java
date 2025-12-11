package com.example.eureka.entrepreneurship.port.out;

import com.example.eureka.general.domain.model.TiposEmprendimientos;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ITiposEmprendimientoRepository extends JpaRepository<TiposEmprendimientos,Integer> {
}
