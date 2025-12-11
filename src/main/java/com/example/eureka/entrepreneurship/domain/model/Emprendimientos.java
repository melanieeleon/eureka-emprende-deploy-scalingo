package com.example.eureka.entrepreneurship.domain.model;

import com.example.eureka.auth.domain.Usuarios;
import com.example.eureka.general.domain.model.Ciudades;
import com.example.eureka.general.domain.model.TiposEmprendimientos;
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
@Table(name = "emprendimientos")
public class Emprendimientos {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "nombre_comercial", nullable = false)
    private String nombreComercial;

    @Column(name = "anio_creacion", nullable = false)
    private LocalDateTime anioCreacion;

    @Column(name = "activo_emprendimiento", nullable = false)
    private Boolean activoEmprendimiento;

    @Column(name = "acepta_datos_publicos", nullable = true)
    private Boolean aceptaDatosPublicos;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion", nullable = true)
    private LocalDateTime fechaActualizacion;

    @Column(name = "estado_emprendimiento",nullable = false)
    private String estadoEmprendimiento;

    @Column(name = "estatus_emprendimiento")
    private Boolean estatusEmprendimiento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuarios usuarios;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ciudad", nullable = false)
    private Ciudades ciudades;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_emprendimiento", nullable = false)
    private TiposEmprendimientos tiposEmprendimientos;

}
