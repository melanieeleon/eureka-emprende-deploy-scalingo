package com.example.eureka.entrepreneurship.port.out;

import com.example.eureka.entrepreneurship.domain.model.EmprendimientoParticipacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IEmprendimientoParticicipacionComunidadRepository extends JpaRepository<EmprendimientoParticipacion, Integer> {
    List<EmprendimientoParticipacion> findByEmprendimientoId(Integer emprendimientoId);
    List<EmprendimientoParticipacion> findByEmprendimientoIdIn(List<Integer> emprendimientoIds);


    @Query("SELECT ep FROM EmprendimientoParticipacion ep JOIN FETCH ep.opcionParticipacion WHERE ep.emprendimiento.id = :emprendimientoId")
    java.util.List<EmprendimientoParticipacion> findByEmprendimientoIdFetchOpcion(@org.springframework.data.repository.query.Param("emprendimientoId") Integer emprendimientoId);
}
