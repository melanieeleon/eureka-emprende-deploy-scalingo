package com.example.eureka.entrepreneurship.port.out;

import com.example.eureka.entrepreneurship.domain.model.TiposDescripcionEmprendimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IEmprendimientosDescripcionRepository extends JpaRepository<TiposDescripcionEmprendimiento, Integer> {

    List<TiposDescripcionEmprendimiento> findByEmprendimientoIdIn(List<Integer> emprendimientoIds);

    List<TiposDescripcionEmprendimiento> findByEmprendimientoId(Integer emprendimientoId);
}
