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
@Table(name = "descripciones")
public class Descripciones {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "descripcion_pregunta", nullable = false)
    private String descripcion;

    @Column(name = "cantidad_maxima_caracteres", nullable = false)
    private Integer cantidadMaximaCaracteres;

    @Column(name = "estado", nullable = false)
    private Boolean esActivo;
}
