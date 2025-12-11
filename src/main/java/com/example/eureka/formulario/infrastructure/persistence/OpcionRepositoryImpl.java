package com.example.eureka.formulario.infrastructure.persistence;

import com.example.eureka.formulario.domain.model.Opciones;
import com.example.eureka.formulario.port.out.IOpcionRepository;
import org.springframework.stereotype.Component;

@Component
public class OpcionRepositoryImpl implements IOpcionRepository {

    private final OpcionJpaRepository opcionJpaRepository;

    public OpcionRepositoryImpl(OpcionJpaRepository opcionJpaRepository) {
        this.opcionJpaRepository = opcionJpaRepository;
    }

    @Override
    public Opciones save(Opciones opcion) {
        return opcionJpaRepository.save(opcion);
    }

    @Override
    public Opciones findById(Long id) {
        return opcionJpaRepository.findById(id).orElse(null);
    }
}
