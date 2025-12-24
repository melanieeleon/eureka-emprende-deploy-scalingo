package com.example.eureka.entrepreneurship.infrastructure.dto.shared;

import lombok.Data;

@Data
public class TipoDescripcionEmprendimientoDTO {

    private Integer id;
    private Integer descripcionId;
    private String respuesta;
    private EmprendimientoResponseDTO emprendimiento;

}
