package com.example.eureka.general.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "declaraciones_finales")
public class DeclaracionesFinales {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "declaracion", nullable = false, columnDefinition = "TEXT")
    private String declaracion;

    @Column(name = "obligatoria", nullable = false)
    private Boolean obligatoria;
}