package com.example.eureka.autoevaluacion.service;

import com.example.eureka.domain.model.Emprendimientos;
import com.example.eureka.domain.model.Respuesta;
import com.example.eureka.autoevaluacion.dto.EmprendimientoInfo;
import com.example.eureka.autoevaluacion.dto.RespuestaFormularioDTO;

import java.util.List;

public interface AutoevaluacionService {

    List<Respuesta> findAllByEmprendimientos(Emprendimientos emprendimientos);

    Respuesta findById(Long idRespuesta);

    List<EmprendimientoInfo> obtenerEmprendimientos();

    boolean existsByEmprendimientos(Emprendimientos emprendimientos);

    List<RespuestaFormularioDTO> obtenerRespuestasPorEmprendimiento(Long idEmprendimiento);

    Respuesta saveRespuesta(Respuesta respuesta);

}
