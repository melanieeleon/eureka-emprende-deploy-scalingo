package com.example.eureka.articulo.port.out;

import com.example.eureka.articulo.domain.model.ArticulosBlog;
import com.example.eureka.shared.enums.EstadoArticulo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface IArticuloRepository {

    List<ArticulosBlog> findWithFilters(
            EstadoArticulo estado,
            Integer idTag,
            String titulo,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin
    );

    Page<ArticulosBlog> findWithFilters(
            EstadoArticulo estado,
            Integer idTag,
            String titulo,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            Pageable pageable
    );

    ArticulosBlog save(ArticulosBlog articulo);

    Optional<ArticulosBlog> findById(Integer id);


    Page<ArticulosBlog> findAll(Specification<ArticulosBlog> spec, Pageable pageable);

    void deleteById(Integer id);
}