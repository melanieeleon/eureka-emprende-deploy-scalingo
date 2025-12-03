package com.example.eureka.autoevaluacion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AutoevaluacionDTO {

    private Integer id;
    private String nombre;
    private String categoria;
    private String tipo;
    private String fecha;
    private String hora;
    private String estado;

}
