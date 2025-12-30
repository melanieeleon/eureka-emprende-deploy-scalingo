package com.example.eureka.autoevaluacion.infrastructure.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListadoAutoevaluacionDTO {

    private Integer idAutoevaluacion;
    private Integer idValoracionOrigen;
    private Boolean esAutoevaluacion;
    private LocalDateTime fechaRespuesta;
    private Integer idFormulario;
    private String formulario;
    private Integer idEmprendimiento;
    private String emprendimiento;
    private ValoracionResumenDTO valoracionOrigen;

    // getters y setters
    public Integer getIdAutoevaluacion() {
        return idAutoevaluacion;
    }

    public void setIdAutoevaluacion(Integer idAutoevaluacion) {
        this.idAutoevaluacion = idAutoevaluacion;
    }

    public Integer getIdValoracionOrigen() {
        return idValoracionOrigen;
    }

    public void setIdValoracionOrigen(Integer idValoracionOrigen) {
        this.idValoracionOrigen = idValoracionOrigen;
    }

    public Boolean getEsAutoevaluacion() {
        return esAutoevaluacion;
    }

    public void setEsAutoevaluacion(Boolean esAutoevaluacion) {
        this.esAutoevaluacion = esAutoevaluacion;
    }

    public LocalDateTime getFechaRespuesta() {
        return fechaRespuesta;
    }

    public void setFechaRespuesta(LocalDateTime fechaRespuesta) {
        this.fechaRespuesta = fechaRespuesta;
    }

    public Integer getIdFormulario() {
        return idFormulario;
    }

    public void setIdFormulario(Integer idFormulario) {
        this.idFormulario = idFormulario;
    }

    public String getFormulario() {
        return formulario;
    }

    public void setFormulario(String formulario) {
        this.formulario = formulario;
    }

    public Integer getIdEmprendimiento() {
        return idEmprendimiento;
    }

    public void setIdEmprendimiento(Integer idEmprendimiento) {
        this.idEmprendimiento = idEmprendimiento;
    }

    public String getEmprendimiento() {
        return emprendimiento;
    }

    public void setEmprendimiento(String emprendimiento) {
        this.emprendimiento = emprendimiento;
    }

    public ValoracionResumenDTO getValoracionOrigen() {
        return valoracionOrigen;
    }

    public void setValoracionOrigen(ValoracionResumenDTO valoracionOrigen) {
        this.valoracionOrigen = valoracionOrigen;
    }
}