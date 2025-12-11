package com.example.eureka.formulario.port.out;

import com.example.eureka.entrepreneurship.domain.model.OpcionRespuesta;
import com.example.eureka.formulario.domain.model.Opciones;
import com.example.eureka.autoevaluacion.domain.model.Respuesta;

import java.util.List;

public interface IOpcionRespuestaRepository {

    List<OpcionRespuesta> findAllByRespuesta(Respuesta respuesta);

    List<OpcionRespuesta> findAllByOpciones(Opciones opciones);

    OpcionRespuesta save(OpcionRespuesta opcionRespuesta);

}
