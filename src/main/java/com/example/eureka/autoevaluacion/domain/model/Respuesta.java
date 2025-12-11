package com.example.eureka.autoevaluacion.domain.model;


import com.example.eureka.entrepreneurship.domain.model.Emprendimientos;
import com.example.eureka.formulario.domain.model.Formulario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"formulario","emprendimientos", "respuesta"})  // Excluir relación del toString
@Table(name = "respuesta")
public class Respuesta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_respuesta")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_formulario", nullable = false)
    private Formulario formulario;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_emprendimiento", nullable = false)
    private Emprendimientos emprendimientos;

    @Column(name = "fecha_respuesta", nullable = false)
    private LocalDateTime fechaRespuesta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_valoracion_origen", nullable = false)
    private Respuesta respuesta;

    @Column(name = "es_autoevaluacion")
    private Boolean esAutoEvaluacion;





}
