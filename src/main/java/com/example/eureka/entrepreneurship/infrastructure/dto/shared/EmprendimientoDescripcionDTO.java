package com.example.eureka.entrepreneurship.infrastructure.dto.shared;

import lombok.Data;

@Data
public class EmprendimientoDescripcionDTO {

    private String tipoDescripcion;

    private String descripcion;

    private Integer maxCaracteres;

    private Boolean obligatorio;

    private Integer idEmprendimiento;

    private Integer emprendimientoId;
}
