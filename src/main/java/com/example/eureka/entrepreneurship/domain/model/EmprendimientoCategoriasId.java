package com.example.eureka.entrepreneurship.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class EmprendimientoCategoriasId implements Serializable {
    @Column(name = "emprendimiento_id")
    private Integer emprendimientoId;

    @Column(name = "categoria_id")
    private Integer categoriaId;
}