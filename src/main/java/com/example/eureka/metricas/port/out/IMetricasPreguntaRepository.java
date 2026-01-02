package com.example.eureka.metricas.port.out;

import com.example.eureka.entrepreneurship.domain.model.Emprendimientos;
import com.example.eureka.formulario.domain.model.Pregunta;
import com.example.eureka.metricas.domain.MetricasPregunta;
import com.example.eureka.metricas.infrastructure.dto.RankingGlobalDTO;
import com.example.eureka.metricas.infrastructure.dto.RankingPreguntaDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IMetricasPreguntaRepository extends JpaRepository<MetricasPregunta, Integer> {


    MetricasPregunta findTopByOrderByValoracionDesc();

    MetricasPregunta findTopByOrderByValoracionAsc();

    Optional<MetricasPregunta> findByEmprendimientosAndPregunta(Emprendimientos emprendimientos,
                                                                Pregunta pregunta);

    List<MetricasPregunta> findAllByEmprendimientosAndPregunta(Emprendimientos emprendimientos,
                                                               Pregunta pregunta);

    // Ranking global por emprendimiento (promedio de todas las preguntas)
    @Query("""
           select new com.example.eureka.metricas.infrastructure.dto.RankingGlobalDTO(
               e.id,
               e.nombreComercial,
               avg(mp.valoracion)
           )
           from MetricasPregunta mp
           join mp.emprendimientos e
           group by e.id, e.nombreComercial
           """)
    Page<RankingGlobalDTO> rankingGlobal(Pageable pageable);

    // Ranking por pregunta (opcionalmente filtrando por tipo de emprendimiento)
    @Query("""
           select new com.example.eureka.metricas.infrastructure.dto.RankingPreguntaDTO(
               e.id,
               e.nombreComercial,
               p.idPregunta,
               p.pregunta,
               mp.valoracion
           )
           from MetricasPregunta mp
           join mp.emprendimientos e
           join mp.pregunta p
           where p.idPregunta = :idPregunta
             and (:idTipoEmprendimiento is null or e.tiposEmprendimientos.id = :idTipoEmprendimiento)
           order by mp.valoracion desc
           """)
    Page<RankingPreguntaDTO> rankingPorPregunta(@Param("idPregunta") Long idPregunta,
                                                @Param("idTipoEmprendimiento") Long idTipoEmprendimiento,
                                                Pageable pageable);
}
