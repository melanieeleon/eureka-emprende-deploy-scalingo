package com.example.eureka.notificacion.domain;

import com.example.eureka.shared.enums.Prioridad;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tipos_notificacion")
public class TipoNotificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_notificacion")
    private Long id;

    @Column(name = "codigo", nullable = false, unique = true, length = 50)
    private String codigo; // SOLICITUD_APROBADA, SOLICITUD_RECHAZADA, etc.

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "plantilla_titulo", nullable = false, length = 200)
    private String plantillaTitulo;

    @Column(name = "plantilla_mensaje", nullable = false, columnDefinition = "TEXT")
    private String plantillaMensaje;

    @Column(name = "icono", length = 50)
    private String icono;

    @Column(name = "color", length = 20)
    private String color;

    @Column(name = "prioridad", length = 20)
    @Enumerated(EnumType.STRING)
    private Prioridad prioridad;

    @Column(name = "activo")
    private Boolean activo = true;
}