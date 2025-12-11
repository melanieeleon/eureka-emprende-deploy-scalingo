package com.example.eureka.formulario.infrastructure.persistence;

import com.example.eureka.formulario.domain.model.Opciones;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OpcionJpaRepository extends JpaRepository<Opciones,Long> {
}
