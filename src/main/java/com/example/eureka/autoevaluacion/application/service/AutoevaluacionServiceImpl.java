package com.example.eureka.autoevaluacion.application.service;

import com.example.eureka.autoevaluacion.infrastructure.dto.RespuestaResponseDTO;
import com.example.eureka.entrepreneurship.domain.model.Emprendimientos;
import com.example.eureka.autoevaluacion.domain.model.Respuesta;
import com.example.eureka.autoevaluacion.infrastructure.dto.EmprendimientoInfo;
import com.example.eureka.autoevaluacion.infrastructure.dto.RespuestaFormularioDTO;
import com.example.eureka.autoevaluacion.port.out.IAutoevaluacionRepository;
import com.example.eureka.autoevaluacion.port.in.AutoevaluacionService;
import com.example.eureka.entrepreneurship.port.out.IEmprendimientosRepository;
import com.example.eureka.formulario.domain.model.Formulario;
import com.example.eureka.formulario.port.out.IFormularioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AutoevaluacionServiceImpl implements AutoevaluacionService {

    private final IAutoevaluacionRepository valoracionRepository;
    private final IFormularioRepository formularioRepository;
    private final IEmprendimientosRepository  emprendimientosRepository;

    @Override
    public List<Respuesta> findAllByEmprendimientos(Emprendimientos emprendimientos) {
        return valoracionRepository.findAllByEmprendimientos(emprendimientos);
    }

    @Override
    public Respuesta findById(Long idRespuesta) {
        return valoracionRepository.findById(idRespuesta.intValue()).orElse(null);
    }

    @Override
    public Page<EmprendimientoInfo> obtenerEmprendimientos(Pageable pageable) {
        return valoracionRepository.obtenerEmprendimientos(pageable);
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
    public Respuesta saveRespuesta(RespuestaResponseDTO respuesta){
        Formulario fm = formularioRepository.findById(respuesta.getIdFormulario().longValue()).orElse(null);
        Respuesta idR = valoracionRepository.findById(respuesta.getIdRespuesta().intValue()).orElse(null);
        Emprendimientos emp = emprendimientosRepository.findById(respuesta.getIdEmprendimiento().intValue()).orElse(null);

        Respuesta data = new Respuesta();
        data.setRespuesta(idR);
        data.setEmprendimientos(emp);
        data.setFormulario(fm);
        data.setFechaRespuesta(respuesta.getFechaRespuesta());
        data.setEsAutoEvaluacion(respuesta.getEsAutoevaluacion());
        return valoracionRepository.save(data);
    }
}
