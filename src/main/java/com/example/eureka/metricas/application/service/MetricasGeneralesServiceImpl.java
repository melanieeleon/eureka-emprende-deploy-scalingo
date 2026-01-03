package com.example.eureka.metricas.application.service;

import com.example.eureka.entrepreneurship.domain.model.EmprendimientoCategorias;
import com.example.eureka.entrepreneurship.domain.model.Emprendimientos;
import com.example.eureka.entrepreneurship.port.out.ICategoriaRepository;
import com.example.eureka.entrepreneurship.port.out.IEmprendimientoCategoriasRepository;
import com.example.eureka.entrepreneurship.port.out.IEmprendimientosRepository;
import com.example.eureka.general.domain.model.Categorias;
import com.example.eureka.general.infrastructure.dto.CategoriasDTO;
import com.example.eureka.metricas.domain.MetricasGenerales;
import com.example.eureka.metricas.domain.MetricasPregunta;
import com.example.eureka.metricas.infrastructure.dto.MetricaPreguntaDTO;
import com.example.eureka.metricas.infrastructure.dto.MetricasGeneralesDTO;
import com.example.eureka.metricas.port.in.MetricasGeneralesService;
import com.example.eureka.metricas.port.out.IMetricasGeneralesRepository;
import com.example.eureka.metricas.port.out.IMetricasPreguntaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MetricasGeneralesServiceImpl implements MetricasGeneralesService {

    private final IMetricasGeneralesRepository metricasGeneralesRepository;
    private final IEmprendimientosRepository emprendimientosRepository;
    private final IEmprendimientoCategoriasRepository emprendimientoCategoriasRepository;
    private final ICategoriaRepository categoriaRepository;
    private final IMetricasPreguntaRepository  metricasPreguntaRepository;



    @Override
    public MetricasGeneralesDTO findTopByOrderByVistasDesc() {
        MetricasGenerales metricasGenerales = metricasGeneralesRepository.findTopByOrderByVistasDesc().orElse(null);
        return toDTO(metricasGenerales);
    }

    @Override
    public MetricasGeneralesDTO findTopByOrderByVistasAsc() {
        MetricasGenerales metricasGenerales = metricasGeneralesRepository.findTopByOrderByVistasAsc().orElse(null);
        return toDTO(metricasGenerales);
    }

    @Override
    public MetricaPreguntaDTO findTopByOrderByNivelValoracionDesc() {
        MetricasPregunta metricasPregunta = metricasPreguntaRepository.findTopByOrderByValoracionDesc();
        return metricaPreguntaDTO(metricasPregunta);
    }

    @Override
    public MetricaPreguntaDTO findTopByOrderByNivelValoracionAsc() {
        MetricasPregunta metricasPregunta = metricasPreguntaRepository.findTopByOrderByValoracionAsc();
        return metricaPreguntaDTO(metricasPregunta);
    }

    @Override
    public MetricasGeneralesDTO save(MetricasGeneralesDTO metricasGenerales) {
        MetricasGenerales metricasGenerales1 = metricasGeneralesRepository.save(toEntity(metricasGenerales));
        return toDTO(metricasGenerales1);
    }

    @Override
    public MetricasGeneralesDTO findById(Integer id) {
        MetricasGenerales metricasGenerales = metricasGeneralesRepository.findById(id).orElse(null);
        return toDTO(metricasGenerales);
    }

    @Override
    public HashMap<String, Object> findTopByOrderByVistasCategoriaDesc() {
        List<Object[]> rows = metricasGeneralesRepository.findCategoriasConVistas();

        List<HashMap<String, Object>> lista = new ArrayList<>();

        for (Object[] row : rows) {
            Categorias categorias = (Categorias) row[0];
            Long totalVistas = (Long) row[1];

            CategoriasDTO cat = new CategoriasDTO();
            cat.setId(categorias.getId());
            cat.setDescripcion(categorias.getDescripcion());
            cat.setNombre(categorias.getNombre());
            cat.setIdMultimedia(categorias.getMultimedia().getId());
            cat.setUrlImagen(categorias.getMultimedia().getUrlArchivo());

            HashMap<String, Object> item = new HashMap<>();
            item.put("categoria", cat);
            item.put("vistas", totalVistas);

            lista.add(item);
        }

        HashMap<String, Object> resultado = new HashMap<>();
        resultado.put("categorias", lista); // ahora es listado, no solo una

        return resultado;
    }


    MetricasGeneralesDTO toDTO(MetricasGenerales metricasGenerales) {
        MetricasGeneralesDTO dto = new MetricasGeneralesDTO();
        dto.setId(metricasGenerales.getId());
        dto.setVistas(metricasGenerales.getVistas());
        dto.setFechaRegistro(metricasGenerales.getFechaRegistro());

        if (metricasGenerales.getEmprendimientos() != null) {
            Emprendimientos emp = metricasGenerales.getEmprendimientos();
            dto.setIdEmprendimiento(emp.getId());
            dto.setNombreEmprendimiento(emp.getNombreComercial()); // ← aquí mapeas el nombre
        }

        return dto;
    }


    MetricaPreguntaDTO metricaPreguntaDTO(MetricasPregunta metricasPregunta) {
        MetricaPreguntaDTO metricaPreguntaDTO = new MetricaPreguntaDTO();
        metricaPreguntaDTO.setId(metricasPregunta.getId());
        metricaPreguntaDTO.setIdPregunta(metricasPregunta.getPregunta().getIdPregunta().intValue());
        metricaPreguntaDTO.setValoracion(metricasPregunta.getValoracion());
        metricaPreguntaDTO.setFechaRegistro(metricasPregunta.getFechaRegistro());
        return metricaPreguntaDTO;
    }

    MetricasGenerales toEntity(MetricasGeneralesDTO metricasGeneralesDTO) {
        Emprendimientos emp = emprendimientosRepository.findById(metricasGeneralesDTO.getIdEmprendimiento()).orElse(null);
        MetricasGenerales metricasGenerales = new MetricasGenerales();
        metricasGenerales.setId(metricasGeneralesDTO.getId());
        metricasGenerales.setVistas(metricasGeneralesDTO.getVistas());
        metricasGenerales.setEmprendimientos(emp);
        metricasGenerales.setFechaRegistro(metricasGeneralesDTO.getFechaRegistro());
        return metricasGenerales;
    }
}
