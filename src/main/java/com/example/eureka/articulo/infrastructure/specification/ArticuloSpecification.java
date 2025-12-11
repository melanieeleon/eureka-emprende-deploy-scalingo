package com.example.eureka.articulo.infrastructure.specification;

import com.example.eureka.shared.enums.EstadoArticulo;
import com.example.eureka.articulo.domain.model.ArticulosBlog;
import com.example.eureka.articulo.domain.model.TagsBlog;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ArticuloSpecification {

    public static Specification<ArticulosBlog> conFiltros(
            EstadoArticulo estado,
            Integer idTag,
            String titulo,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (estado != null) {
                predicates.add(criteriaBuilder.equal(root.get("estado"), estado));
            }

            if (idTag != null) {
                Join<ArticulosBlog, TagsBlog> tagsJoin = root.join("tags", JoinType.INNER);
                predicates.add(criteriaBuilder.equal(tagsJoin.get("idTag"), idTag));
            }

            if (titulo != null && !titulo.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("titulo")),
                        "%" + titulo.toLowerCase() + "%"
                ));
            }

            if (fechaInicio != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("fechaCreacion"), fechaInicio
                ));
            }

            if (fechaFin != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("fechaCreacion"), fechaFin
                ));
            }

            query.distinct(true);
            query.orderBy(criteriaBuilder.desc(root.get("fechaCreacion")));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}