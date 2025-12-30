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


    public List<MetricasPregunta> findAllByEmprendimientosAndPregunta(Emprendimientos emp, Pregunta pregunta) {
        // solo si aún quieres devolver lista
        return metricasPreguntaRepository.findAll().stream()
                .filter(m -> m.getEmprendimientos().equals(emp) && m.getPregunta().equals(pregunta))
                .toList();
    }

    // MÉTODO IMPORTANTE: guardar o actualizar la métrica de una pregunta
    public MetricasPregunta guardarOActualizar(
            Emprendimientos emp,
            Pregunta pregunta,
            Double valoracion) {

        MetricasPregunta mp = metricasPreguntaRepository
                .findByEmprendimientosAndPregunta(emp, pregunta)
                .orElseGet(MetricasPregunta::new);

        mp.setEmprendimientos(emp);
        mp.setPregunta(pregunta);
        mp.setValoracion(valoracion);
        mp.setFechaRegistro(LocalDateTime.now());

        return metricasPreguntaRepository.save(mp);
        // Si mp ya tiene id -> UPDATE; si no -> INSERT (regla de JPA) [web:120][web:121]
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
