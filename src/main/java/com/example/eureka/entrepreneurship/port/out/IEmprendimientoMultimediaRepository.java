package com.example.eureka.entrepreneurship.port.out;

import com.example.eureka.entrepreneurship.domain.model.EmprendimientoMultimedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IEmprendimientoMultimediaRepository extends JpaRepository<EmprendimientoMultimedia, Long> {

    /**
     * Buscar multimedia por emprendimiento con JOIN FETCH para evitar N+1
     */
    @Query("SELECT em FROM EmprendimientoMultimedia em " +
            "JOIN FETCH em.multimedia " +
            "WHERE em.emprendimiento.id = :emprendimientoId")
    List<EmprendimientoMultimedia> findByEmprendimientoId(@Param("emprendimientoId") Integer emprendimientoId);

    /**
     * Buscar multimedia por emprendimiento y tipo
     */
    @Query("SELECT em FROM EmprendimientoMultimedia em " +
            "JOIN FETCH em.multimedia " +
            "WHERE em.emprendimiento.id = :emprendimientoId " +
            "AND em.tipo = :tipo")
    List<EmprendimientoMultimedia> findByEmprendimientoIdAndTipo(
            @Param("emprendimientoId") Integer emprendimientoId,
            @Param("tipo") String tipo);

    /**
     * Buscar multimedia por lista de IDs de emprendimientos (para batch loading)
     */
    @Query("SELECT em FROM EmprendimientoMultimedia em " +
            "JOIN FETCH em.multimedia " +
            "WHERE em.emprendimiento.id IN :emprendimientoIds")
    List<EmprendimientoMultimedia> findByEmprendimientoIdIn(@Param("emprendimientoIds") List<Integer> emprendimientoIds);

    /**
     * Eliminar multimedia por emprendimiento
     */
    void deleteByEmprendimientoId(Integer emprendimientoId);

    /**
     * Eliminar multimedia específica de un emprendimiento
     */
    void deleteByEmprendimientoIdAndMultimediaId(Integer emprendimientoId, Long multimediaId);
}
