package com.example.eureka.entrepreneurship.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "informacion_representante")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InformacionRepresentante {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name="nombre")
    private String nombre;

    @Column(name="apellido")
    private String apellido;

    @Column(name="correo_corporativo")
    private String correoCorporativo;

    @Column(name="correo_personal")
    private String correoPersonal;

    @Column(name="telefono")
    private String telefono;

    @Column(name="identificacion")
    private String identificacion;

    @Column(name="carrera")
    private String carrera;

    @Column(name="semestre")
    private String semestre;

    @Column(name = "fecha_graduacion")
    private LocalDateTime fechaGraduacion;

    @Column(name = "tiene_parientes_uees")
    private Boolean tieneParientesUees;

    @Column(name = "nombre_pariente")
    private String nombrePariente;

    @Column(name = "area_pariente")
    private String areaPariente;

    @Column(name = "integrantes_equipo")
    private String integrantesEquipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emprendimiento_id", nullable = false)
    private Emprendimientos emprendimiento;

}
