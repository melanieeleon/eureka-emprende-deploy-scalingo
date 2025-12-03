package com.example.eureka.autoevaluacion.service.impl;

import com.example.eureka.domain.model.Emprendimientos;
import com.example.eureka.domain.model.Respuesta;
import com.example.eureka.autoevaluacion.dto.EmprendimientoInfo;
import com.example.eureka.autoevaluacion.dto.RespuestaFormularioDTO;
import com.example.eureka.autoevaluacion.repository.IAutoevaluacionRepository;
import com.example.eureka.autoevaluacion.service.AutoevaluacionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AutoevaluacionServiceImpl implements AutoevaluacionService {

    private final IAutoevaluacionRepository valoracionRepository;


    @Override
    public List<Respuesta> findAllByEmprendimientos(Emprendimientos emprendimientos) {
        return valoracionRepository.findAllByEmprendimientos(emprendimientos);
    }

    @Override
    public Respuesta findById(Long idRespuesta) {
        return valoracionRepository.findById(idRespuesta.intValue()).orElse(null);
    }

    @Override
    public List<EmprendimientoInfo> obtenerEmprendimientos() {
        return valoracionRepository.obtenerEmprendimientos();
    }

    @Override
    public boolean existsByEmprendimientos(Emprendimientos emprendimientos) {
        return valoracionRepository.existsByEmprendimientos(emprendimientos);
    }

    @Override
    public List<RespuestaFormularioDTO> obtenerRespuestasPorEmprendimiento(Long idEmprendimiento) {
        return valoracionRepository.obtenerRespuestasPorEmprendimiento(idEmprendimiento);
    }

    @Override
    public Respuesta saveRespuesta(Respuesta respuesta){
        return valoracionRepository.save(respuesta);
    }
}
