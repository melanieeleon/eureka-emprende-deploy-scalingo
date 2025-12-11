package com.example.eureka.entrepreneurship.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tipos_descripcion_emprendimiento")
public class TiposDescripcionEmprendimiento {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "tipo_descripcion", nullable = false, columnDefinition = "TEXT")
    private String tipoDescripcion;

    @Column(name = "descripcion", nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "max_caracteres")
    private Integer maxCaracteres;

    @Column(name = "obligatorio", nullable = false)
    private Boolean obligatorio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_emprendimiento", nullable = false)
    private Emprendimientos emprendimiento;
}
