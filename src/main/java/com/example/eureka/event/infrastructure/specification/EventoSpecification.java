package com.example.eureka.event.infrastructure.specification;

import com.example.eureka.shared.enums.EstadoEvento;
import com.example.eureka.shared.enums.TipoEvento;
import com.example.eureka.event.domain.model.Eventos;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EventoSpecification {

    public static Specification<Eventos> conFiltros(
            String titulo,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            EstadoEvento estado,
            TipoEvento tipoEvento,
            Integer mes,
            Integer idEmprendimiento) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (titulo != null && !titulo.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("titulo")),
                        "%" + titulo.toLowerCase() + "%"
                ));
            }

            if (fechaInicio != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("fechaEvento"), fechaInicio));
            }
            if (fechaFin != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("fechaEvento"), fechaFin));
            }

            if (estado != null) {
                predicates.add(criteriaBuilder.equal(root.get("estadoEvento"), estado));
            }

            if (tipoEvento != null) {
                predicates.add(criteriaBuilder.equal(root.get("tipoEvento"), tipoEvento));
            }

            if (mes != null) {
                predicates.add(
                        criteriaBuilder.equal(
                                criteriaBuilder.function(
                                        "date_part",
                                        Integer.class,
                                        criteriaBuilder.literal("month"),
                                        root.get("fechaEvento")
                                ),
                                mes
                        )
                );
            }

            if (idEmprendimiento != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("emprendimiento").get("id"),
                        idEmprendimiento
                ));
            }

            query.distinct(true);
            query.orderBy(criteriaBuilder.asc(root.get("fechaEvento")));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Eventos> porUsuarioEmprendedor(Integer idUsuario) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("emprendimiento").get("usuarios").get("id"),
                        idUsuario
                );
    }
}