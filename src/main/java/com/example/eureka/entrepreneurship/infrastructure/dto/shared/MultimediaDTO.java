package com.example.eureka.entrepreneurship.infrastructure.dto.shared;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para representar multimedia asociada a emprendimientos
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MultimediaDTO {

    /**
     * ID único de la multimedia
     */
    private Integer id;

    /**
     * URL del archivo en S3
     * Ejemplo: https://mi-bucket.s3.amazonaws.com/abc-123.jpg
     */
    private String urlArchivo;

    /**
     * Nombre original del archivo
     * Ejemplo: logo-empresa.png
     */
    private String nombreActivo;

    /**
     * Tipo de multimedia en el contexto del emprendimiento
     * Valores: LOGO, PORTADA, GALERIA, VIDEO, DOCUMENTO, CERTIFICADO
     */
    private String tipo;

    /**
     * Tipo MIME del archivo
     * Ejemplo: image/jpeg, video/mp4, application/pdf
     */
    private String mimeType;

    /**
     * Tamaño del archivo en kilobytes
     */
    private Long tamanoKb;

    /**
     * Fecha y hora de subida del archivo
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fechaSubida;
}