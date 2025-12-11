package com.example.eureka.entrepreneurship.domain.model;

import com.example.eureka.auth.domain.Usuarios;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "solicitudes_emprendimiento")
public class SolicitudEmprendimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Column(name = "id_solicitud", nullable = false)
    private Integer id;

    @Column(name = "estado")
    private String estado;

    @Column(name = "observaciones_admin")
    private String observaciones;

    @Column(name = "fecha_solicitud")
    private LocalDateTime fechaSolicitud;

    @Column(name = "fecha_respuesta")
    private LocalDateTime fechaRespuesta;

    @ManyToOne
    @JoinColumn(name = "emprendimiento_id")
    private Emprendimientos emprendimiento;

    @ManyToOne
    @JoinColumn(name = "usuario_solicitante_id")
    private Usuarios usuario;

    @ManyToOne
    @JoinColumn(name = "administrador_revisor_id")
    private Usuarios usuarioAdministrador;
}
