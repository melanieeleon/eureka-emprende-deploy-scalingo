package com.example.eureka.entrepreneurship.port.out;

import com.example.eureka.entrepreneurship.domain.model.TiposPresenciaDigital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IEmprendimientoPresenciaDigitalRepository extends JpaRepository<TiposPresenciaDigital, Integer> {
    List<TiposPresenciaDigital> findByEmprendimientoId(Integer emprendimientoId);
    List<TiposPresenciaDigital> findByEmprendimientoIdIn(List<Integer> emprendimientoIds);

}
