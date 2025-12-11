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
@Table(name = "emprendimiento_multimedia")
public class EmprendimientoMultimedia {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emprendimiento_id", nullable = false)
    private Emprendimientos emprendimiento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "multimedia_id", nullable = false)
    private Multimedia multimedia;

    @Column(name = "tipo", nullable = false, length = 100)
    private String tipo;
}
