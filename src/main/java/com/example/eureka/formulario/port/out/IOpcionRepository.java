package com.example.eureka.formulario.port.out;

import com.example.eureka.formulario.domain.model.Opciones;

public interface IOpcionRepository {

    Opciones save(Opciones opcion);
    Opciones findById(Long id);
}
