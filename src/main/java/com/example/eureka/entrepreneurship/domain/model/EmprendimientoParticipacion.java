package com.example.eureka.entrepreneurship.domain.model;

import com.example.eureka.general.domain.model.OpcionesParticipacionComunidad;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "emprendimiento_participacion")
public class EmprendimientoParticipacion {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "respuesta", nullable = false)
    private Boolean respuesta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emprendimiento_id", nullable = false)
    private Emprendimientos emprendimiento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "opcion_participacion_id", nullable = false)
    private OpcionesParticipacionComunidad opcionParticipacion;
}
