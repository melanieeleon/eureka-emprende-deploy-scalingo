package com.example.eureka.entrepreneurship.infrastructure.dto.shared;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SolicitudEmprendimientoDTO {
    private Integer id;
    private String estado;
    private String observaciones;
    private LocalDateTime fechaSolicitud;
    private LocalDateTime fechaRespuesta;
    private Integer emprendimientoId;
    private Integer usuarioId;
    private Integer usuarioAdministradorId;

}

