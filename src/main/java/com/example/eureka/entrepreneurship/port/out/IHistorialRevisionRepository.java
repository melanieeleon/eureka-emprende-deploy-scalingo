package com.example.eureka.entrepreneurship.port.out;

import com.example.eureka.entrepreneurship.domain.model.HistorialRevision;
import com.example.eureka.entrepreneurship.domain.model.SolicitudAprobacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IHistorialRevisionRepository extends JpaRepository<HistorialRevision, Long> {

    List<HistorialRevision> findBySolicitudOrderByFechaAccionDesc(SolicitudAprobacion solicitud);

    List<HistorialRevision> findBySolicitudIdOrderByFechaAccionDesc(Integer solicitudId);
}