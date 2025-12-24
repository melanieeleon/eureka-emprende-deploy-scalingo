package com.example.eureka.entrepreneurship.infrastructure.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudAprobacionListadoDTO {
    private Integer id;                 // id de la solicitud
    private Integer emprendimientoId;
    private String nombreEmprendimiento;
    private String tipoSolicitud;
    private String estadoSolicitud;

    private Integer ciudadId;
    private String nombreCiudad;

    private String tipoEmprendimiento;
    private LocalDateTime anioCreacion;

    private LocalDateTime fechaSolicitud;
    private String nombreSolicitante;
}