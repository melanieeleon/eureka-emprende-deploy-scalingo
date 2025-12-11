package com.example.eureka.general.infrastructure.dto;

import lombok.Data;

@Data
public class MultimediaDTO {
    private String nombreActivo;
    private String urlArchivo;
    private String tipoMultimedia;
    private String subTipo;
    private String mimeType;
    private Integer tamanioKb;
    private String descripcion;
    private String fechaSubida;
}
