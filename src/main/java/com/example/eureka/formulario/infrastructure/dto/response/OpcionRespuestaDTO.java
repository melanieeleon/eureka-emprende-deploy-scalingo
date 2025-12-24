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
    private Integer id;
    private Integer idRespuesta;
    @JsonIgnore
    private List<OpcionResponseDTO> opciones;
    private Integer idPregunta;
    private Integer valorescala;
    private Integer idEmprendimientos;
}
