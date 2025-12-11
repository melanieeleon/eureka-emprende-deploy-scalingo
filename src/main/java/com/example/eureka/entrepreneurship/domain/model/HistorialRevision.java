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
@Table(name = "historial_revisiones")
public class HistorialRevision {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historial")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_solicitud", nullable = false)
    private SolicitudAprobacion solicitud;

    @Column(name = "accion", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private AccionRevision accion;

    @Column(name = "comentario", columnDefinition = "TEXT")
    private String comentario;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "cambios_realizados", columnDefinition = "jsonb")
    private Map<String, Object> cambiosRealizados;

    @Column(name = "fecha_accion", nullable = false)
    private LocalDateTime fechaAccion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuarios usuario;

    public enum AccionRevision {
        ENVIO,
        APROBACION,
        RECHAZO,
        OBSERVACIONES,
        MODIFICACION
    }

    @PrePersist
    protected void onCreate() {
        if (fechaAccion == null) {
            fechaAccion = LocalDateTime.now();
        }
    }
}