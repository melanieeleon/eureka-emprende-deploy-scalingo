package com.example.eureka.autoevaluacion.infrastructure.dto;

import java.time.LocalDateTime;

public class RespuestaResponseDTO {

    private Integer id;
    private Integer idFormulario;
    private Integer idEmprendimiento;
    private LocalDateTime fechaRespuesta;
    private Integer idRespuesta;
    private Boolean esAutoevaluacion;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIdFormulario() {
        return idFormulario;
    }

    public void setIdFormulario(Integer idFormulario) {
        this.idFormulario = idFormulario;
    }

    public Integer getIdEmprendimiento() {
        return idEmprendimiento;
    }

    public void setIdEmprendimiento(Integer idEmprendimiento) {
        this.idEmprendimiento = idEmprendimiento;
    }

    public LocalDateTime getFechaRespuesta() {
        return fechaRespuesta;
    }

    public void setFechaRespuesta(LocalDateTime fechaRespuesta) {
        this.fechaRespuesta = fechaRespuesta;
    }

    public Integer getIdRespuesta() {
        return idRespuesta;
    }

    public void setIdRespuesta(Integer idRespuesta) {
        this.idRespuesta = idRespuesta;
    }

    public Boolean getEsAutoevaluacion() {
        return esAutoevaluacion;
    }

    public void setEsAutoevaluacion(Boolean esAutoevaluacion) {
        this.esAutoevaluacion = esAutoevaluacion;
    }
}
