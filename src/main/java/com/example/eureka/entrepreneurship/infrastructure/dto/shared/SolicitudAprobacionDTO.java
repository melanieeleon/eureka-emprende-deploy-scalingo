package com.example.eureka.entrepreneurship.infrastructure.dto.shared;

import com.example.eureka.entrepreneurship.domain.model.SolicitudAprobacion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudAprobacionDTO {
    private Integer id;
    private Integer emprendimientoId;
    private String nombreEmprendimiento;
    private SolicitudAprobacion.TipoSolicitud tipoSolicitud;
    private SolicitudAprobacion.EstadoSolicitud estadoSolicitud;
    private String observaciones;
    private String motivoRechazo;
    private LocalDateTime fechaSolicitud;
    private LocalDateTime fechaRespuesta;
    private String nombreSolicitante;
    private String nombreRevisor;
}