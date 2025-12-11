package com.example.eureka.entrepreneurship.infrastructure.dto.shared;

import lombok.Data;

@Data
public class TipoDescripcionEmprendimientoDTO {

    private Integer id;
    private String tipoDescripcion;
    private String descripcion;
    private Integer maxCaracteres;
    private Boolean obligatorio;
    private EmprendimientoResponseDTO emprendimiento;

}
