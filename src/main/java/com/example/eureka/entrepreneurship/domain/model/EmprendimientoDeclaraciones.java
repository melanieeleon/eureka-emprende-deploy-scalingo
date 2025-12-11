package com.example.eureka.entrepreneurship.domain.model;

import com.example.eureka.general.domain.model.DeclaracionesFinales;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "emprendimiento_declaraciones")
public class EmprendimientoDeclaraciones {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "aceptada", nullable = false)
    private Boolean aceptada;

    @Column(name = "fecha_aceptacion")
    private LocalDateTime fechaAceptacion;

    @Column(name = "nombre_firma")
    private String nombreFirma;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emprendimiento_id", nullable = false)
    private Emprendimientos emprendimiento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "declaracion_id", nullable = false)
    private DeclaracionesFinales declaracion;
}