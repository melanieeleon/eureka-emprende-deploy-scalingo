package com.example.eureka.entrepreneurship.infrastructure.dto.shared;

import com.example.eureka.general.infrastructure.dto.CategoriasDTO;

import lombok.Data;

@Data
public class EmprendimientoCategoriaDTO {

    private EmprendimientoDTO emprendimiento;

    private CategoriasDTO categoria;

    private String nombreCategoria;
}
