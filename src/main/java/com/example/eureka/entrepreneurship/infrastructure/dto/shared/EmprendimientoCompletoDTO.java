package com.example.eureka.entrepreneurship.infrastructure.dto.shared;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO que contiene TODOS los datos del emprendimiento y sus relaciones
 * Se usa para guardar en JSONB en solicitudes_aprobacion
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmprendimientoCompletoDTO {

    // Datos básicos del emprendimiento
    private String nombreComercial;
    private LocalDateTime anioCreacion;
    private Boolean activoEmprendimiento;
    private Boolean aceptaDatosPublicos;
    private Integer tipoEmprendimientoId;
    private Integer ciudadId;

    // Información del representante (si existe)
    private InformacionRepresentanteDTO informacionRepresentante;

    // Relaciones
    private List<EmprendimientoCategoriaDTO> categorias;
    private List<EmprendimientoDescripcionDTO> descripciones;
    private List<EmprendimientoMetricasDTO> metricas;
    private List<EmprendimientoPresenciaDigitalDTO> presenciasDigitales;
    private List<EmprendimientoDeclaracionesDTO> declaracionesFinales;
    private List<EmprendimientoParticipacionDTO> participacionesComunidad;
    private UsuarioDTO usuario;
}