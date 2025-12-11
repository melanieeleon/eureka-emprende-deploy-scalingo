package com.example.eureka.entrepreneurship.domain.model;

import com.example.eureka.metricas.domain.MetricasBasicas;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "emprendimiento_metricas")
public class EmprendimientoMetricas {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "valor", nullable = false)
    private String valor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emprendimiento_id", nullable = false)
    private Emprendimientos emprendimiento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "metrica_id", nullable = false)
    private MetricasBasicas metrica;
}
