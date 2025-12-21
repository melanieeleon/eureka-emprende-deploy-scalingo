package com.example.eureka.entrepreneurship.infrastructure.dto.shared;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EmprendimientoDTO {

    private Integer id;

    private String correoComercial;

    private String correoUees;

    private String identificacion;

    private String parienteDirecto;

    private String nombreComercialEmprendimiento;

    private LocalDateTime fechaCreacion;

    private Integer ciudad;

    private Integer provinia;

    private Boolean estadoEmpredimiento;


    private String tipoEmprendimiento;

    private Integer tipoEmprendimientoId;

    private Boolean datosPublicos;

}
