package com.example.eureka.metricas.port.in;

import com.example.eureka.entrepreneurship.domain.model.Emprendimientos;
import com.example.eureka.metricas.domain.MetricasGenerales;
import com.example.eureka.metricas.domain.MetricasPregunta;
import com.example.eureka.metricas.infrastructure.dto.MetricaPreguntaDTO;
import com.example.eureka.metricas.infrastructure.dto.MetricasGeneralesDTO;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

public interface MetricasGeneralesService {

    MetricasGeneralesDTO findTopByOrderByVistasDesc();

    MetricasGeneralesDTO findTopByOrderByVistasAsc();

    MetricaPreguntaDTO findTopByOrderByNivelValoracionDesc();

    MetricaPreguntaDTO findTopByOrderByNivelValoracionAsc();

    MetricasGeneralesDTO save(MetricasGeneralesDTO metricasGenerales);

    MetricasGeneralesDTO findById(Integer id);


    HashMap<String, Object> findTopByOrderByVistasCategoriaDesc();


}
