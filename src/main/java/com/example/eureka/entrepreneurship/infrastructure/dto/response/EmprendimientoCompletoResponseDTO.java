package com.example.eureka.entrepreneurship.infrastructure.dto.response;

import com.example.eureka.entrepreneurship.infrastructure.dto.shared.*;
import com.example.eureka.general.infrastructure.dto.MultimediaDTO;

import java.util.List;

public class EmprendimientoCompletoResponseDTO {
    private EmprendimientoDTO emprendimiento;
    private List<EmprendimientoCategoriaDTO> categorias;
    private List<EmprendimientoDescripcionDTO> descripciones;
    private List<EmprendimientoPresenciaDigitalDTO> presenciasDigitales;
    private List<MultimediaDTO> imagenes;
    private List<EmprendimientoMetricasDTO> metricas;
    private List<EmprendimientoParticipacionDTO> participacionesComunidad;
    private List<EmprendimientoDeclaracionesDTO> declaracionesFinales;

    // Información del representante
    private InformacionRepresentanteDTO representante;
}
