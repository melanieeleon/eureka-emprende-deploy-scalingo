package com.example.eureka.entrepreneurship.infrastructure.dto.response;

import com.example.eureka.entrepreneurship.infrastructure.dto.shared.*;
import com.example.eureka.general.infrastructure.dto.CiudadDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmprendimientoDetallesDTO {

    private String nombreComercial;
    private LocalDateTime anioCreacion;
    private Boolean activoEmprendimiento;
    private Boolean aceptaDatosPublicos;
    private Integer tipoEmprendimientoId;
    private String tipoEmprendimiento;
    private Integer tipoPersonaJuridicaId;
    private String tipoPersonaJuridicaDescripcion;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private CiudadDTO ciudad;

    private InformacionRepresentanteDTO informacionRepresentante;
    private List<CategoriaListadoDTO> categorias;
    private List<EmprendimientoDescripcionDTO> descripciones;
    private List<MultimediaListadoDTO> multimedia;

    private List<EmprendimientoMetricasDTO> metricas;
    private List<EmprendimientoPresenciaDigitalDTO> presenciasDigitales;
    private List<EmprendimientoDeclaracionesDTO> declaracionesFinales;
    private List<EmprendimientoParticipacionDTO> participacionesComunidad;





}
