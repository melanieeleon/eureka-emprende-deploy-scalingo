package com.example.eureka.general.infrastructure.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class CategoriaRequestDTO {

    private Integer id;
    private String nombre;
    private String descripcion;
    private MultipartFile archivo;

}
