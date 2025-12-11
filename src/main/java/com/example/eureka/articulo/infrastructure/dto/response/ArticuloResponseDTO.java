package com.example.eureka.articulo.infrastructure.dto.response;

import com.example.eureka.shared.enums.EstadoArticulo;
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
public class ArticuloResponseDTO {

    private Integer idArticulo;
    private String titulo;
    private String descripcionCorta;
    private String contenido;
    private EstadoArticulo estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;
    private Integer idImagen;
    private String urlImagen;
    private Integer idUsuario;
    private String nombreUsuario;
    private List<TagDTO> tags;
}