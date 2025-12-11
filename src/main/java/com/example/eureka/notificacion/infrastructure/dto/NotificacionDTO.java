package com.example.eureka.notificacion.infrastructure.dto;

import com.example.eureka.shared.enums.Prioridad;
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
public class NotificacionDTO {
    private Integer id;
    private String titulo;
    private String mensaje;
    private String enlace;
    private Boolean leida;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaLectura;
    private Prioridad prioridad;

    // Info del tipo
    private String tipoNombre;
    private String icono;
    private String color;

    // Metadata adicional
    private Map<String, Object> metadata;

    // Referencias
    private Integer emprendimientoId;
    private String nombreEmprendimiento;
    private Integer solicitudId;
}