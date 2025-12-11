package com.example.eureka.entrepreneurship.domain.model;

import com.example.eureka.auth.domain.Usuarios;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "solicitudes_aprobacion")
public class SolicitudAprobacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_solicitud")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_emprendimiento", nullable = false)
    private Emprendimientos emprendimiento;

    @Column(name = "tipo_solicitud", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private TipoSolicitud tipoSolicitud;

    @Column(name = "estado_solicitud", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private EstadoSolicitud estadoSolicitud;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "datos_propuestos", columnDefinition = "jsonb", nullable = false)
    private Map<String, Object> datosPropuestos;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "datos_originales", columnDefinition = "jsonb")
    private Map<String, Object> datosOriginales;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "motivo_rechazo", columnDefinition = "TEXT")
    private String motivoRechazo;

    @Column(name = "fecha_solicitud", nullable = false)
    private LocalDateTime fechaSolicitud;

    @Column(name = "fecha_respuesta")
    private LocalDateTime fechaRespuesta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_solicitante", nullable = false)
    private Usuarios usuarioSolicitante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_revisor")
    private Usuarios usuarioRevisor;

    public enum TipoSolicitud {
        CREACION,
        ACTUALIZACION
    }

    public enum EstadoSolicitud {
        PENDIENTE,
        APROBADO,
        RECHAZADO,
        EN_REVISION
    }

    @PrePersist
    protected void onCreate() {
        if (fechaSolicitud == null) {
            fechaSolicitud = LocalDateTime.now();
        }
        if (estadoSolicitud == null) {
            estadoSolicitud = EstadoSolicitud.PENDIENTE;
        }
    }
}