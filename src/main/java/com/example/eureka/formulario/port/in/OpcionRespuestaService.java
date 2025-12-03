package com.example.eureka.formulario.port.in;

import com.example.eureka.domain.model.OpcionRespuesta;
import com.example.eureka.domain.model.Opciones;
import com.example.eureka.domain.model.Respuesta;
import com.example.eureka.formulario.infrastructure.dto.response.OpcionRespuestaDTO;

import java.util.List;

public interface OpcionRespuestaService {

    List<OpcionRespuestaDTO> findAllByRespuesta(Respuesta respuesta);

    List<OpcionRespuestaDTO> findAllByOpciones(Opciones opciones);

    OpcionRespuestaDTO save(OpcionRespuesta opcionRespuesta);
}
