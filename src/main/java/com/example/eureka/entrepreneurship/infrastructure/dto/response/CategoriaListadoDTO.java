package com.example.eureka.entrepreneurship.infrastructure.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
@JsonIgnoreProperties(ignoreUnknown = true)

@NoArgsConstructor
@Data
public class CategoriaListadoDTO {
    private Integer id;
    private String nombre;

    public CategoriaListadoDTO(Integer id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }
}
