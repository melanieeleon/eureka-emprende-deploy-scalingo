package com.example.eureka.formulario.infrastructure.dto.response;

import com.example.eureka.entrepreneurship.domain.model.Emprendimientos;
import com.example.eureka.formulario.domain.model.Opciones;
import com.example.eureka.autoevaluacion.domain.model.Respuesta;
import com.example.eureka.formulario.domain.model.Pregunta;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class OpcionRespuestaDTO {
    private Integer id;                     // id de OpcionRespuesta
    private Integer idRespuesta;
    private Integer idPregunta;
    private String pregunta;               // texto de la pregunta
    private Integer valorescala;
    private Integer idEmprendimientos;
    private List<OpcionResponseDTO> opciones;
}
