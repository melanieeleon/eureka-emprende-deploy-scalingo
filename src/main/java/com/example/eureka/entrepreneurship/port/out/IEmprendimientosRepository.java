package com.example.eureka.entrepreneurship.port.out;

import com.example.eureka.entrepreneurship.domain.model.Emprendimientos;
import com.example.eureka.auth.domain.Usuarios;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IEmprendimientosRepository extends JpaRepository<Emprendimientos, Integer> {
    List<Emprendimientos> findByUsuarios(Usuarios usuarios);
    List<Emprendimientos> findByNombreComercialContainingIgnoreCase(String nombre);
    List<Emprendimientos> findByTiposEmprendimientos_SubTipo(String tiposEmprendimientosSubTipo);
    List<Emprendimientos> findByNombreComercialContainingIgnoreCaseAndTiposEmprendimientos_TipoContainingIgnoreCase(String nombreComercial, String tiposEmprendimientosTipo);

    List<Emprendimientos> findByTiposEmprendimientos_Tipo(String tiposEmprendimientosTipo);

    @Query("SELECT DISTINCT e FROM Emprendimientos e " +
            "LEFT JOIN e.tiposEmprendimientos te " +
            "LEFT JOIN EmprendimientoCategorias ec ON ec.emprendimiento.id = e.id " +
            "LEFT JOIN ec.categoria c " +
            "LEFT JOIN e.ciudades ci " +
            "WHERE (COALESCE(:nombre, '') = '' OR LOWER(e.nombreComercial) LIKE LOWER(CONCAT('%', :nombre, '%'))) " +
            "AND (COALESCE(:tipo, '') = '' OR LOWER(te.tipo) LIKE LOWER(CONCAT('%', :tipo, '%'))) " +
            "AND (COALESCE(:categoria, '') = '' OR LOWER(c.nombre) LIKE LOWER(CONCAT('%', :categoria, '%'))) " +
            "AND (COALESCE(:ciudad, '') = '' OR LOWER(ci.nombreCiudad) LIKE LOWER(CONCAT('%', :ciudad, '%')))")
    List<Emprendimientos> findByFiltros(@Param("nombre") String nombre,
                                        @Param("tipo") String tipo,
                                        @Param("categoria") String categoria,
                                        @Param("ciudad") String ciudad);

    @Query("SELECT DISTINCT e FROM Emprendimientos e " +
            "LEFT JOIN e.tiposEmprendimientos te " +
            "LEFT JOIN EmprendimientoCategorias ec ON ec.emprendimiento.id = e.id " +
            "LEFT JOIN ec.categoria c " +
            "LEFT JOIN e.ciudades ci " +
            "WHERE (COALESCE(:nombre, '') = '' OR LOWER(e.nombreComercial) LIKE LOWER(CONCAT('%', :nombre, '%'))) " +
            "AND (COALESCE(:tipo, '') = '' OR LOWER(te.tipo) LIKE LOWER(CONCAT('%', :tipo, '%'))) " +
            "AND (COALESCE(:categoria, '') = '' OR LOWER(c.nombre) LIKE LOWER(CONCAT('%', :categoria, '%'))) " +
            "AND (COALESCE(:ciudad, '') = '' OR LOWER(ci.nombreCiudad) LIKE LOWER(CONCAT('%', :ciudad, '%')))")
    org.springframework.data.domain.Page<Emprendimientos> findByFiltros(
            @Param("nombre") String nombre,
            @Param("tipo") String tipo,
            @Param("categoria") String categoria,
            @Param("ciudad") String ciudad,
            org.springframework.data.domain.Pageable pageable
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
