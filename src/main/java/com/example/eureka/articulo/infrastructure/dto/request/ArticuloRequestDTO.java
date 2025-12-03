package com.example.eureka.articulo.infrastructure.dto.request;

import com.example.eureka.articulo.infrastructure.specification.ValidationGroups;
import com.example.eureka.domain.enums.EstadoArticulo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticuloRequestDTO {

    @NotBlank(message = "El título es obligatorio", groups = {ValidationGroups.OnCreate.class, ValidationGroups.OnUpdate.class})
    private String titulo;

    private String descripcionCorta;

    @NotBlank(message = "El contenido es obligatorio", groups = {ValidationGroups.OnCreate.class, ValidationGroups.OnUpdate.class})
    private String contenido;

    @NotNull(message = "El ID de imagen es obligatorio", groups = {ValidationGroups.OnCreate.class})
    private MultipartFile imagen;

    private List<Integer> idsTags; // IDs de tags existentes
    private List<String> nombresTags; // Nombres de tags nuevos

    private EstadoArticulo estado; // NUEVO: estado inicial (BORRADOR o PUBLICADO)

}