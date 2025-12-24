package com.example.eureka.entrepreneurship.domain.model;


import com.example.eureka.autoevaluacion.domain.model.Respuesta;
import com.example.eureka.formulario.domain.model.Opciones;
import com.example.eureka.formulario.domain.model.Pregunta;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"respuesta", "opciones", "emprendimiento", "pregunta"})  // Excluir relación del toString
@Table(name = "opcion_respuesta")
public class OpcionRespuesta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_respuesta", nullable = false)
    private Respuesta respuesta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_opcion", nullable = false)
    private Opciones opciones;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pregunta")
    private Pregunta pregunta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_emprendimiento")
    private Emprendimientos emprendimiento;

    @Column(name = "valor_escala", nullable = false)
    private Integer valorescala;





}
