package com.example.eureka.formulario.port.out;

import com.example.eureka.domain.model.Opciones;
import com.example.eureka.formulario.domain.model.Opcion;

public interface IOpcionRepository {

    Opciones save(Opciones opcion);
    Opciones findById(Long id);
}
