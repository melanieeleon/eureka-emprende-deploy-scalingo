package com.example.eureka.general.infrastructure.dto;

import lombok.Data;

@Data
public class CiudadDTO {

    private Integer id;
    private String nombreCiudad;
    private ProvinciaDTO provincia;
}
