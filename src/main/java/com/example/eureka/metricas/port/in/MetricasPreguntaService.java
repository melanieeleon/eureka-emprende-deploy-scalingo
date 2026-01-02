package com.example.eureka.metricas.port.in;

import com.example.eureka.entrepreneurship.domain.model.Emprendimientos;
import com.example.eureka.formulario.domain.model.Pregunta;
import com.example.eureka.metricas.domain.MetricasPregunta;
import com.example.eureka.metricas.infrastructure.dto.RankingGlobalDTO;
import com.example.eureka.metricas.infrastructure.dto.RankingPreguntaDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface MetricasPreguntaService {

    MetricasPregunta save(MetricasPregunta metricasPregunta);
    List<MetricasPregunta> findAll();
    MetricasPregunta findById(Integer id);
    void deleteById(Integer id);
    List<MetricasPregunta> findAllByEmprendimientosAndPregunta(Emprendimientos emprendimientos,
                                                               Pregunta pregunta);

    Optional<MetricasPregunta> findByEmprendimientosAndPregunta(Emprendimientos emprendimientos,
                                                                Pregunta pregunta);
    MetricasPregunta guardarOActualizar(Emprendimientos emprendimiento,
                                        Pregunta pregunta,
                                        Double valoracion, Long cantidadRespuestasNuevas);

    Page<RankingGlobalDTO> obtenerRankingGlobal(Pageable pageable);

    Page<RankingPreguntaDTO> obtenerRankingPorPregunta(Long idPregunta,
                                                       Long idTipoEmprendimiento,
                                                       Pageable pageable);
}
