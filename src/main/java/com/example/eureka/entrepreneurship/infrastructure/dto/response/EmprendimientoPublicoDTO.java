package com.example.eureka.entrepreneurship.infrastructure.dto.response;

import com.example.eureka.entrepreneurship.infrastructure.dto.shared.EmprendimientoDescripcionDTO;
import com.example.eureka.entrepreneurship.infrastructure.dto.shared.EmprendimientoPresenciaDigitalDTO;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class EmprendimientoPublicoDTO {

    private Integer id;
    private String nombreComercial;
    private LocalDateTime anioCreacion;
    private Boolean activoEmprendimiento;
    private Boolean aceptaDatosPublicos;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private String estadoEmprendimiento;

    private Integer usuarioId;
    private String nombreUsuario;

    private Integer ciudadId;
    private String nombreCiudad;

    private Integer tipoEmprendimientoId;
    private String nombreTipoEmprendimiento;

    private List<CategoriaListadoDTO> categorias;
    private List<EmprendimientoDescripcionDTO> descripciones;
    private List<EmprendimientoPresenciaDigitalDTO> presenciasDigitales;
    private List<MultimediaListadoDTO> multimedia;
}
