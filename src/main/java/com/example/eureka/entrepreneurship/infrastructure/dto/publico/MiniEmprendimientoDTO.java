package com.example.eureka.entrepreneurship.infrastructure.dto.publico;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MiniEmprendimientoDTO {

    Integer id;
    String nombreComercial;
    String ciudad;
    List<String> categorias;
    String descripcion;

}
