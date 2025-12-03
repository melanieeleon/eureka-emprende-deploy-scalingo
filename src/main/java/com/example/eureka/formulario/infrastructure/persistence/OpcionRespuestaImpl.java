package com.example.eureka.formulario.infrastructure.persistence;

import com.example.eureka.domain.model.OpcionRespuesta;
import com.example.eureka.domain.model.Opciones;
import com.example.eureka.domain.model.Respuesta;
import com.example.eureka.formulario.port.out.IOpcionRespuestaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OpcionRespuestaImpl implements IOpcionRespuestaRepository {

    private final OpcionRespuestaJpaRepository opcionRespuestaJpaRepository;

    public OpcionRespuestaImpl(OpcionRespuestaJpaRepository opcionRespuestaJpaRepository) {
        this.opcionRespuestaJpaRepository = opcionRespuestaJpaRepository;
    }


    @Override
    public List<OpcionRespuesta> findAllByRespuesta(Respuesta respuesta) {
        return opcionRespuestaJpaRepository.findAllByRespuesta(respuesta);
    }

    @Override
    public List<OpcionRespuesta> findAllByOpciones(Opciones opciones) {
        return opcionRespuestaJpaRepository.findAllByOpciones(opciones);
    }

    @Override
    public OpcionRespuesta save(OpcionRespuesta opcionRespuesta) {
        return opcionRespuestaJpaRepository.save(opcionRespuesta);
    }
}
