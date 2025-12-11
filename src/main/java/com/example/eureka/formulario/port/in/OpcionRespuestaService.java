package com.example.eureka.formulario.port.in;

import com.example.eureka.entrepreneurship.domain.model.OpcionRespuesta;
import com.example.eureka.formulario.domain.model.Opciones;
import com.example.eureka.autoevaluacion.domain.model.Respuesta;
import com.example.eureka.formulario.infrastructure.dto.response.OpcionRespuestaDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OpcionRespuestaService {

    Page<OpcionRespuestaDTO> findAllByRespuesta(Respuesta respuesta, Pageable pageable);

    Page<OpcionRespuestaDTO> findAllByOpciones(Opciones opciones, Pageable pageable);

    OpcionRespuestaDTO save(OpcionRespuesta opcionRespuesta);
}
