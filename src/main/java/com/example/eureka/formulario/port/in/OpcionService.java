package com.example.eureka.formulario.port.in;

import com.example.eureka.formulario.domain.model.Opciones;
import com.example.eureka.formulario.infrastructure.dto.response.OpcionResponseDTO;

public interface OpcionService {

    Opciones findById(Long id);
    OpcionResponseDTO save(Opciones opcion);
}
