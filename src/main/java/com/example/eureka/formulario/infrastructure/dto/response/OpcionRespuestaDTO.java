package com.example.eureka.formulario.infrastructure.dto.response;

import com.example.eureka.domain.model.Opciones;
import com.example.eureka.domain.model.Respuesta;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpcionRespuestaDTO {
    private Integer id;
    private Respuesta respuesta;
    private Opciones opciones;
    private Integer valorescala;
}
