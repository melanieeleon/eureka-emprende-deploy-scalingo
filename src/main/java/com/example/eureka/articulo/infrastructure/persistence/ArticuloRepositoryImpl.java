package com.example.eureka.articulo.infrastructure.persistence;

import com.example.eureka.articulo.domain.model.ArticulosBlog;
import com.example.eureka.articulo.infrastructure.specification.ArticuloSpecification;
import com.example.eureka.articulo.port.out.IArticuloRepository;
import com.example.eureka.shared.enums.EstadoArticulo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class ArticuloRepositoryImpl implements IArticuloRepository {

    private final ArticuloJpaRepository jpaRepository;

    public ArticuloRepositoryImpl(ArticuloJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<ArticulosBlog> findWithFilters(
            EstadoArticulo estado,
            Integer idTag,
            String titulo,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin) {

        Specification<ArticulosBlog> spec = ArticuloSpecification.conFiltros(
                estado, idTag, titulo, fechaInicio, fechaFin
        );

        return jpaRepository.findAll(spec);
    }

    @Override
    public Page<ArticulosBlog> findWithFilters(
            EstadoArticulo estado,
            Integer idTag,
            String titulo,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            Pageable pageable) {

        Specification<ArticulosBlog> spec = ArticuloSpecification.conFiltros(
                estado, idTag, titulo, fechaInicio, fechaFin
        );

        return jpaRepository.findAll(spec, pageable);
    }

    @Override
    public Page<ArticulosBlog> findAll(Specification<ArticulosBlog> spec, Pageable pageable) {
        return jpaRepository.findAll(spec, pageable);
    }

    @Override
    public ArticulosBlog save(ArticulosBlog articulo) {
        return jpaRepository.save(articulo);
    }

    @Override
    public Optional<ArticulosBlog> findById(Integer id) {
        return jpaRepository.findById(id);
    }


    @Override
    public void deleteById(Integer id) {
        jpaRepository.deleteById(id);
    }
}