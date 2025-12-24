package com.example.eureka.entrepreneurship.domain.model;

import com.example.eureka.general.domain.model.Descripciones;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "descripcion_emprendimiento")
public class DescripcionEmprendimiento {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_descripciones")
    private Descripciones descripciones;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_emprendimiento")
    private Emprendimientos emprendimiento;

    @Column(name = "respuesta")
    private String respuesta;

}
