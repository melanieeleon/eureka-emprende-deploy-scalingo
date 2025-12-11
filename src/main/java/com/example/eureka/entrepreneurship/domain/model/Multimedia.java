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
@Table(name = "multimedia")
public class Multimedia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "nombre_activo", nullable = false)
    private String nombreActivo;

    @Column(name = "url_archivo", nullable = false)
    private String urlArchivo;

    @Column(name = "descripcion", nullable = false)
    private String descripcion;

}
