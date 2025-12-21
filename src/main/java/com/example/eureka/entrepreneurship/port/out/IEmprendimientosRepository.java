package com.example.eureka.entrepreneurship.port.out;

import com.example.eureka.entrepreneurship.domain.model.Emprendimientos;
import com.example.eureka.auth.domain.Usuarios;
import com.example.eureka.entrepreneurship.infrastructure.dto.response.EmprendimientoListadoResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IEmprendimientosRepository extends JpaRepository<Emprendimientos, Integer> {

    @Query("""
select distinct new com.example.eureka.entrepreneurship.infrastructure.dto.response.EmprendimientoListadoResponseDTO(
    e.id,
    e.nombreComercial,
    e.fechaCreacion,
    ci.id,
    ci.nombreCiudad,
    p.id,
    p.nombre,
    e.estatusEmprendimiento,
    te.tipo,
    te.subTipo,
    te.id
)
from Emprendimientos e
left join e.tiposEmprendimientos te
left join e.ciudades ci
left join ci.provincias p
left join EmprendimientoCategorias ec on ec.emprendimiento.id = e.id
left join ec.categoria c
where lower(e.nombreComercial) like lower(concat('%', coalesce(:nombre,  e.nombreComercial), '%'))
  and lower(te.tipo)            like lower(concat('%', coalesce(:tipo,    te.tipo),          '%'))
  and lower(te.subTipo)         like lower(concat('%', coalesce(:subtipo, te.subTipo),       '%'))
  and lower(ci.nombreCiudad)    like lower(concat('%', coalesce(:ciudad,  ci.nombreCiudad),  '%'))
  and lower(c.nombre)           like lower(concat('%', coalesce(:categoria, c.nombre),       '%'))
  and e.estadoEmprendimiento = 'PUBLICADO'
""")
    Page<EmprendimientoListadoResponseDTO> findByFiltrosListado(
            @Param("nombre") String nombre,
            @Param("tipo") String tipo,          // Emprendimiento / Programa / etc.
            @Param("subtipo") String subtipo,
            @Param("categoria") String categoria,
            @Param("ciudad") String ciudad,
            Pageable pageable
    );

    @Query("""
   select new com.example.eureka.entrepreneurship.infrastructure.dto.response.EmprendimientoListadoResponseDTO(
       e.id,
       e.nombreComercial,
       e.fechaCreacion,
       c.id,
       c.nombreCiudad,
       p.id,
       p.nombre,
       e.estatusEmprendimiento,
       te.tipo,
       te.subTipo,
       te.id
   )
   from Emprendimientos e
   left join e.ciudades c
   left join c.provincias p
   left join e.tiposEmprendimientos te
   where e.usuarios = :usuario
""")
    Page<EmprendimientoListadoResponseDTO> findByUsuarioListado(
            @Param("usuario") Usuarios usuario,
            Pageable pageable
    );



    @Query("SELECT DISTINCT e FROM Emprendimientos e " +
            "LEFT JOIN e.tiposEmprendimientos te " +
            "LEFT JOIN EmprendimientoCategorias ec ON ec.emprendimiento.id = e.id " +
            "LEFT JOIN ec.categoria c " +
            "LEFT JOIN e.ciudades ci " +
            "WHERE e.estadoEmprendimiento =:estadoEmprendimiento" )
    List<Emprendimientos> findByEstadoEmprendimiento(@Param("estadoEmprendimiento") String estadoEmprendimiento);

    List<Emprendimientos> findByUsuariosAndEstadoEmprendimientoEquals(Usuarios usuarios, String estadoEmprendimiento);

    Emprendimientos findByFechaCreacionIsBetweenAndId(LocalDateTime fechaCreacionAfter, LocalDateTime fechaCreacionBefore, Integer id);

}
