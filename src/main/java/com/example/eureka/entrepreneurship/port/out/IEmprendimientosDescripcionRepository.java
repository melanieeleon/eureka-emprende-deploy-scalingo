package com.example.eureka.entrepreneurship.port.out;

import com.example.eureka.entrepreneurship.domain.model.DescripcionEmprendimiento;
import com.example.eureka.entrepreneurship.domain.model.Emprendimientos;
import com.example.eureka.general.domain.model.Descripciones;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IEmprendimientosDescripcionRepository extends JpaRepository<DescripcionEmprendimiento, Integer> {

    List<DescripcionEmprendimiento> findByEmprendimientoIdIn(List<Integer> emprendimientoIds);

    List<DescripcionEmprendimiento> findByEmprendimientoId(Integer emprendimientoId);

    DescripcionEmprendimiento findByEmprendimientoAndDescripciones(Emprendimientos emprendimiento, Descripciones descripciones);
}
