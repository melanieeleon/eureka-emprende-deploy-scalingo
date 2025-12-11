package com.example.eureka.entrepreneurship.port.out;

import com.example.eureka.general.domain.model.Categorias;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICategoriaRepository extends JpaRepository<Categorias, Integer> {
}