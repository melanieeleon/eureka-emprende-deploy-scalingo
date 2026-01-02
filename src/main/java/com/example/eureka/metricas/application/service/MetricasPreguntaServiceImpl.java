package com.example.eureka.metricas.application.service;

import com.example.eureka.entrepreneurship.domain.model.Emprendimientos;
import com.example.eureka.formulario.domain.model.Pregunta;
import com.example.eureka.metricas.domain.MetricasBasicas;
import com.example.eureka.metricas.domain.MetricasPregunta;
import com.example.eureka.metricas.infrastructure.dto.RankingGlobalDTO;
import com.example.eureka.metricas.infrastructure.dto.RankingPreguntaDTO;
import com.example.eureka.metricas.port.in.MetricasBasicasService;
import com.example.eureka.metricas.port.in.MetricasPreguntaService;
import com.example.eureka.metricas.port.out.IMetricasPreguntaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MetricasPreguntaServiceImpl implements MetricasPreguntaService {

    private final IMetricasPreguntaRepository metricasPreguntaRepository;


    @Override
    public MetricasPregunta save(MetricasPregunta metricasPregunta) {
        return metricasPreguntaRepository.save(metricasPregunta);
    }

    @Override
    public List<MetricasPregunta> findAll() {
        return metricasPreguntaRepository.findAll();
    }

    @Override
    public MetricasPregunta findById(Integer id) {
        return metricasPreguntaRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteById(Integer id) {
        metricasPreguntaRepository.deleteById(id);
    }

    @Override
    public List<MetricasPregunta> findAllByEmprendimientosAndPregunta(Emprendimientos emp, Pregunta pregunta) {
        // solo si aún quieres devolver lista
        return metricasPreguntaRepository.findAll().stream()
                .filter(m -> m.getEmprendimientos().equals(emp) && m.getPregunta().equals(pregunta))
                .toList();
    }

    @Override
    public Optional<MetricasPregunta> findByEmprendimientosAndPregunta(Emprendimientos emp,
                                                                       Pregunta pregunta) {
        return metricasPreguntaRepository.findByEmprendimientosAndPregunta(emp, pregunta);
    }

    @Override
    public MetricasPregunta guardarOActualizar(Emprendimientos emprendimiento,
                                               Pregunta pregunta,
                                               Double promedioNuevo,
                                               Long cantidadRespuestasNuevas) {

        MetricasPregunta metrica = metricasPreguntaRepository
                .findByEmprendimientosAndPregunta(emprendimiento, pregunta)
                .orElseGet(MetricasPregunta::new);

        metrica.setEmprendimientos(emprendimiento);
        metrica.setPregunta(pregunta);

        if (metrica.getId() == null) {
            // primera vez: el promedio es el de esta valoración
            metrica.setValoracion(promedioNuevo);
            metrica.setTotalRespuestas(cantidadRespuestasNuevas.intValue());
        } else {
            // combinar promedio anterior con el nuevo
            double promedioAnterior = metrica.getValoracion();
            Integer totalAnterior = metrica.getTotalRespuestas();

            double sumAnterior = promedioAnterior * totalAnterior;
            double sumNueva = promedioNuevo * cantidadRespuestasNuevas;

            Integer totalNuevo = totalAnterior + cantidadRespuestasNuevas.intValue();
            double promedioAcumulado = totalNuevo > 0
                    ? (sumAnterior + sumNueva) / totalNuevo
                    : 0.0;

            metrica.setValoracion(promedioAcumulado);
            metrica.setTotalRespuestas(totalNuevo);
        }

        metrica.setFechaRegistro(LocalDateTime.now());

        return metricasPreguntaRepository.save(metrica);
    }


    // NUEVO: ranking global
    @Override
    public Page<RankingGlobalDTO> obtenerRankingGlobal(Pageable pageable) {
        return metricasPreguntaRepository.rankingGlobal(pageable);
    }

    // NUEVO: ranking por pregunta
    @Override
    public Page<RankingPreguntaDTO> obtenerRankingPorPregunta(Long idPregunta,
                                                              Long idTipoEmprendimiento,
                                                              Pageable pageable) {
        return metricasPreguntaRepository.rankingPorPregunta(idPregunta, idTipoEmprendimiento, pageable);
    }

}
