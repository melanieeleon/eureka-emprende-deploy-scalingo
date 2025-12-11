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
public class EventoResponseDTO {

    private Integer idEvento;
    private String titulo;
    private String descripcion;
    private LocalDateTime fechaEvento;
    private String lugar;
    private TipoEvento tipoEvento;
    private String linkInscripcion;
    private String direccion;
    private EstadoEvento estadoEvento;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;
    private Integer idEmprendimiento;
    private String nombreEmprendimiento;
    private Integer idMultimedia;
    private boolean activo;
}
