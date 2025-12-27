package com.example.eureka.entrepreneurship.infrastructure.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class MultimediaListadoDTO {
    private int id;
    private String nombreActivo;
    private String urlArchivo;

    public MultimediaListadoDTO(int id,String nombreActivo, String urlArchivo) {
        this.id = id;
        this.nombreActivo = nombreActivo;
        this.urlArchivo = urlArchivo;
    }
}
