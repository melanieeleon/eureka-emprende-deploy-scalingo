package com.example.eureka.entrepreneurship.infrastructure.dto.request;

import com.example.eureka.entrepreneurship.infrastructure.dto.shared.*;
import jakarta.validation.Valid;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class EmprendimientoRequestDTO {

    private Integer usuarioId;

    @Valid
    private EmprendimientoDTO emprendimiento;
    private InformacionRepresentanteDTO informacionRepresentante;
    private String tipoAccion; // "CREAR" o "BORRADOR"
    private List<EmprendimientoCategoriaDTO> categorias;
    private List<EmprendimientoDescripcionDTO> descripciones;
    private List<EmprendimientoMetricasDTO> metricas;
    private List<EmprendimientoPresenciaDigitalDTO> presenciasDigitales;
    private List<EmprendimientoParticipacionDTO> participacionesComunidad;
    private List<EmprendimientoDeclaracionesDTO> declaracionesFinales;

    // NUEVO: Archivos multimedia
    private List<MultipartFile> imagenes; // Imágenes del emprendimiento
    private List<String> tiposMultimedia; // "LOGO", "PORTADA", "GALERIA", etc.
}