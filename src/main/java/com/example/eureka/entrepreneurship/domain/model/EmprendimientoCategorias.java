package com.example.eureka.entrepreneurship.domain.model;

import com.example.eureka.general.domain.model.Categorias;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "emprendimiento_categorias")
public class EmprendimientoCategorias {

    @EmbeddedId
    private EmprendimientoCategoriasId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("emprendimientoId")
    @JoinColumn(name = "emprendimiento_id")
    private Emprendimientos emprendimiento;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("categoriaId")
    @JoinColumn(name = "categoria_id")
    private Categorias categoria;
}