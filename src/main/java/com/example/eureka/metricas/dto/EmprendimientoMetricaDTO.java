package com.example.eureka.metricas.dto;

public class EmprendimientoMetricaDTO {

    private Long id;
    private String valor;
    private Integer idEmprendimiento;
    private Integer idMetricaBasica;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public Integer getIdEmprendimiento() {
        return idEmprendimiento;
    }

    public void setIdEmprendimiento(Integer idEmprendimiento) {
        this.idEmprendimiento = idEmprendimiento;
    }

    public Integer getIdMetricaBasica() {
        return idMetricaBasica;
    }

    public void setIdMetricaBasica(Integer idMetricaBasica) {
        this.idMetricaBasica = idMetricaBasica;
    }
}
