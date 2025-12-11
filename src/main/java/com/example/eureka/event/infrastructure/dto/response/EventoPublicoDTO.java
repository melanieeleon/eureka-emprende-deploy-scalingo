package com.example.eureka.event.infrastructure.dto.response;


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
public class EventoPublicoDTO {
    private Integer idEvento;
    private String titulo;
    private String descripcion;
    private LocalDateTime fechaEvento;
    private LocalDateTime fechaCreacion;
    private TipoEvento tipoEvento;
    private Integer idMultimedia;
    private String urlMultimedia;
}