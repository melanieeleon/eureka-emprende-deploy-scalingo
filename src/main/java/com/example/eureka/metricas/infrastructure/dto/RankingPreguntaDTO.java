package com.example.eureka.metricas.infrastructure.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RankingPreguntaDTO {

    private Integer idEmprendimiento;
    private String nombreEmprendimiento;
    private Long idPregunta;
    private String pregunta;
    private Double promedioPregunta;

    public RankingPreguntaDTO(Integer idEmprendimiento,
                              String nombreEmprendimiento,
                              Long idPregunta,
                              String pregunta,
                              Double promedioPregunta) {
        this.idEmprendimiento = idEmprendimiento;
        this.nombreEmprendimiento = nombreEmprendimiento;
        this.idPregunta = idPregunta;
        this.pregunta = pregunta;
        this.promedioPregunta = promedioPregunta;
    }
}