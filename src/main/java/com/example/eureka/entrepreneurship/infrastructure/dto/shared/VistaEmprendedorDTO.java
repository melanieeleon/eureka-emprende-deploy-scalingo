package com.example.eureka.entrepreneurship.infrastructure.dto.shared;

import com.example.eureka.entrepreneurship.domain.model.SolicitudAprobacion;
import com.example.eureka.entrepreneurship.infrastructure.dto.response.EmprendimientoDetallesDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VistaEmprendedorDTO {
    private EmprendimientoDetallesDTO datosActuales;  // Datos publicados o en borrador
    private EmprendimientoDetallesDTO datosPropuestos;  // Datos pendientes de aprobación
    private String estadoEmprendimiento;
    private SolicitudAprobacion.EstadoSolicitud estadoSolicitud;
    private String observaciones;
    private String motivoRechazo;
    private Boolean tieneSolicitudActiva;
    private Integer solicitudId;
}