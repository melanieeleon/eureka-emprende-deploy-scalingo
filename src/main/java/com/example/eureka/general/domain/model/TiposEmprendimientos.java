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
@Table(name = "tipos_emprendimiento")
public class TiposEmprendimientos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "tipo", nullable = false)
    private String tipo;

    @Column(name = "sub_tipo", nullable = true)
    private String subTipo;
}
