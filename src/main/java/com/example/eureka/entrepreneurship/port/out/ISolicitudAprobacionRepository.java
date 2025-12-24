package com.example.eureka.entrepreneurship.port.out;

import com.example.eureka.entrepreneurship.domain.model.Emprendimientos;
import com.example.eureka.entrepreneurship.domain.model.SolicitudAprobacion;
import com.example.eureka.auth.domain.Usuarios;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ISolicitudAprobacionRepository extends JpaRepository<SolicitudAprobacion, Integer> {

    Page<SolicitudAprobacion> findByEstadoSolicitudOrderByFechaSolicitudAsc(
            SolicitudAprobacion.EstadoSolicitud estado,
            Pageable pageable
    );

    List<SolicitudAprobacion> findByEmprendimientoOrderByFechaSolicitudDesc(
            Emprendimientos emprendimiento
    );

    @Query("SELECT s FROM SolicitudAprobacion s WHERE s.emprendimiento.id = :emprendimientoId " +
            "AND s.estadoSolicitud IN ('PENDIENTE', 'EN_REVISION') " +
            "ORDER BY s.fechaSolicitud DESC")
    Optional<SolicitudAprobacion> findSolicitudActivaByEmprendimientoId(
            @Param("emprendimientoId") Integer emprendimientoId
    );

    List<SolicitudAprobacion> findByUsuarioSolicitanteOrderByFechaSolicitudDesc(
            Usuarios usuario
    );

    Integer countByEstadoSolicitud(SolicitudAprobacion.EstadoSolicitud estado);

    @Query("SELECT s FROM SolicitudAprobacion s " +
            "WHERE s.estadoSolicitud = :estado " +
            "AND s.tipoSolicitud = :tipo " +
            "ORDER BY s.fechaSolicitud ASC")
    List<SolicitudAprobacion> findByEstadoAndTipo(
            @Param("estado") SolicitudAprobacion.EstadoSolicitud estado,
            @Param("tipo") SolicitudAprobacion.TipoSolicitud tipo
    );
}