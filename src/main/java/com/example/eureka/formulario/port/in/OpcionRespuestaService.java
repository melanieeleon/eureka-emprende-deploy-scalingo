package com.example.eureka.formulario.port.in;

import com.example.eureka.autoevaluacion.infrastructure.dto.RespuestaResponseDTO;
import com.example.eureka.formulario.domain.model.Opciones;
import com.example.eureka.autoevaluacion.domain.model.Respuesta;
import com.example.eureka.formulario.infrastructure.dto.response.OpcionRespuestaDTO;
import com.example.eureka.formulario.infrastructure.dto.response.OpcionRespuestaResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OpcionRespuestaService {

    Page<OpcionRespuestaDTO> findAllByRespuesta(Respuesta respuesta, Pageable pageable);

    Page<OpcionRespuestaDTO> findAllByOpciones(Opciones opciones, Pageable pageable);

    List<OpcionRespuestaDTO> save(List<OpcionRespuestaResponseDTO> opcionRespuesta, Integer idUsuario);

    RespuestaResponseDTO generaRespuestaAutoevaluacion(RespuestaResponseDTO respuesta);
}
