package com.example.eureka.autoevaluacion.port.out;

import com.example.eureka.entrepreneurship.domain.model.Emprendimientos;
import com.example.eureka.autoevaluacion.domain.model.Respuesta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IAutoevaluacionRepository extends JpaRepository<Respuesta, Integer> {

    List<Respuesta> findAllByEmprendimientos(Emprendimientos emprendimientos);

/**
    @Query(
            value = """
        SELECT 
            e.id,
            e.nombre_comercial,
            STRING_AGG(c.nombre, ', ') AS categorias,
            te.tipo,
            TO_CHAR(e.fecha_actualizacion, 'YYYY-MM-DD') AS fecha,
            TO_CHAR(e.fecha_actualizacion, 'HH24:MI:SS') AS hora
        FROM emprendimientos e
        RIGHT JOIN emprendimiento_categorias ec ON e.id = ec.emprendimiento_id
        JOIN categorias c ON ec.categoria_id = c.id
        RIGHT JOIN tipos_emprendimiento te ON e.id_tipo_emprendimiento = te.id
        WHERE e.estatus_emprendimiento = TRUE
        GROUP BY 
            e.id, 
            e.nombre_comercial, 
            te.tipo, 
            e.fecha_actualizacion
        """,
            nativeQuery = true
    )
    Page<EmprendimientoInfo> obtenerEmprendimientos(Pageable pageable);

    boolean existsByEmprendimientos(Emprendimientos emprendimientos);

    @Query(
            value = """
        SELECT DISTINCT
            f.nombre AS nombreFormulario,
            e.nombre_comercial AS nombreComercial,
            p.pregunta AS pregunta,
            o.opcion AS opcion,
            ore.valor_escala AS valorEscala
        FROM respuesta r
        INNER JOIN formulario f ON f.id_formulario = r.id_formulario
        INNER JOIN formulario_preguntas fp ON f.id_formulario = fp.id_formulario
        INNER JOIN preguntas p ON fp.id_pregunta = p.id_pregunta
        INNER JOIN opcion_respuesta ore ON ore.id_respuesta = r.id_respuesta 
        INNER JOIN opciones o ON o.id_opcion = ore.id_opcion
        INNER JOIN emprendimientos e ON e.id = r.id_emprendimiento
        WHERE r.id_emprendimiento = :idEmprendimiento
        """,
            nativeQuery = true
    )
    List<RespuestaFormularioDTO> obtenerRespuestasPorEmprendimiento(Long idEmprendimiento);*/

    Page<Respuesta> findAllByEsAutoEvaluacionTrue(Pageable pageable);

}
