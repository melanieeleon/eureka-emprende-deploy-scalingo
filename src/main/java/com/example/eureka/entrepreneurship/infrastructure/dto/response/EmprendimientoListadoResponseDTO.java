package com.example.eureka.entrepreneurship.infrastructure.dto.response;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
@Data
public class EmprendimientoListadoResponseDTO {

    private Integer idEmprendimiento;
    private String nombreComercialEmprendimiento;
    private LocalDateTime fechaCreacion;

    private Integer ciudadId;
    private String ciudadNombre;
    private Integer provinciaId;
    private String provinciaNombre;

    private Boolean estatusEmprendimiento;
    private String estadoEmprendimiento;      // <--- NUEVO
    private String tipoEmprendimiento;
    private String subTipoEmprendimiento;
    private Integer tipoEmprendimientoId;

    private List<CategoriaListadoDTO> categorias;
    private List<MultimediaListadoDTO> multimedia;

    public EmprendimientoListadoResponseDTO(
            Integer idEmprendimiento,
            String nombreComercialEmprendimiento,
            LocalDateTime fechaCreacion,
            Integer ciudadId,
            String ciudadNombre,
            Integer provinciaId,
            String provinciaNombre,
            Boolean estatusEmprendimiento,
            String estadoEmprendimiento,      // <--- NUEVO
            String tipoEmprendimiento,
            String subTipoEmprendimiento,
            Integer tipoEmprendimientoId
    ) {
        this.idEmprendimiento = idEmprendimiento;
        this.nombreComercialEmprendimiento = nombreComercialEmprendimiento;
        this.fechaCreacion = fechaCreacion;
        this.ciudadId = ciudadId;
        this.ciudadNombre = ciudadNombre;
        this.provinciaId = provinciaId;
        this.provinciaNombre = provinciaNombre;
        this.estatusEmprendimiento = estatusEmprendimiento;
        this.estadoEmprendimiento = estadoEmprendimiento;   // <---
        this.tipoEmprendimiento = tipoEmprendimiento;
        this.subTipoEmprendimiento = subTipoEmprendimiento;
        this.tipoEmprendimientoId = tipoEmprendimientoId;
    }
}

