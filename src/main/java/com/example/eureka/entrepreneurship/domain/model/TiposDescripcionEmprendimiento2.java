package com.example.eureka.entrepreneurship.domain.model;

import com.example.eureka.general.domain.model.Descripciones;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tipos_descripcion_emprendimiento_2")
public class TiposDescripcionEmprendimiento2 {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_descripciones", nullable = false)
    private Descripciones DescripcionPregunta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_emprendimiento", nullable = false)
    private Emprendimientos emprendimiento;

    @Column(name = "respuesta", nullable = false)
    private String respuesta;
}
