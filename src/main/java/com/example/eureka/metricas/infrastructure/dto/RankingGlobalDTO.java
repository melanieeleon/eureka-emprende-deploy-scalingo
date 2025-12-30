package com.example.eureka.metricas.infrastructure.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RankingGlobalDTO {

    private Integer idEmprendimiento;
    private String nombreEmprendimiento;
    private Double promedioGlobal;

    public RankingGlobalDTO(Integer idEmprendimiento,
                            String nombreEmprendimiento,
                            Double promedioGlobal) {
        this.idEmprendimiento = idEmprendimiento;
        this.nombreEmprendimiento = nombreEmprendimiento;
        this.promedioGlobal = promedioGlobal;
    }
}