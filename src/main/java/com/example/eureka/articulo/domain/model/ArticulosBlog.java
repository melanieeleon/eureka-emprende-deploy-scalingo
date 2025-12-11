package com.example.eureka.articulo.domain.model;

import com.example.eureka.shared.enums.EstadoArticulo;
import com.example.eureka.entrepreneurship.domain.model.Multimedia;
import com.example.eureka.auth.domain.Usuarios;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "articulos_blog")
@EqualsAndHashCode(exclude = "tags")

public class ArticulosBlog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_articulo", nullable = false)
    private Integer idArticulo;

    @Column(name = "titulo", nullable = false)
    private String titulo;

    @Column(name = "descripcion_corta", columnDefinition = "TEXT")
    private String descripcionCorta;

    @Column(name = "contenido", nullable = false, columnDefinition = "TEXT")
    private String contenido;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoArticulo estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_imagen", nullable = false)
    private Multimedia imagen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuarios usuario;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "articulo_tags",
            joinColumns = @JoinColumn(name = "id_articulo"),
            inverseJoinColumns = @JoinColumn(name = "id_tag")
    )
    private Set<TagsBlog> tags = new HashSet<>();
}
