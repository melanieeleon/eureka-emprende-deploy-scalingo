package com.example.eureka.event.infrastructure.dto.response;

import com.example.eureka.shared.enums.EstadoEvento;
import com.example.eureka.shared.enums.TipoEvento;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventoAdminDTO {
    private Integer idEvento;
    private String titulo;
    private Integer idEmprendimiento;
    private String nombreEmprendimiento;
    private LocalDateTime fechaEvento;
    private LocalDateTime fechaCreacion;
    private EstadoEvento estadoEvento;
    private TipoEvento tipoEvento;
    private boolean activo;
}