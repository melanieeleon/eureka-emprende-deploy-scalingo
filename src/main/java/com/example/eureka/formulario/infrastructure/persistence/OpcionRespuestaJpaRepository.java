package com.example.eureka.formulario.infrastructure.persistence;

import com.example.eureka.entrepreneurship.domain.model.OpcionRespuesta;
import com.example.eureka.formulario.domain.model.Opciones;
import com.example.eureka.autoevaluacion.domain.model.Respuesta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OpcionRespuestaJpaRepository extends JpaRepository<OpcionRespuesta, Integer> {

    @Override
    List<OpcionRespuesta> findAll();

    List<OpcionRespuesta> findAllByRespuesta(Respuesta respuesta);

    List<OpcionRespuesta> findAllByOpciones(Opciones opciones);
    List<OpcionRespuesta> findByRespuestaId(Integer idRespuesta);


}
