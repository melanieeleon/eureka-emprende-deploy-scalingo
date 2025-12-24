package com.example.eureka.formulario.infrastructure.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

public class OpcionRespuestaResponseDTO {

    private Integer idRespuesta;
    private Integer idsPregunta;
    @JsonIgnore
    private List<Integer> idsOpciones;
    private Integer valorescala;
    private Integer idEmprendimiento;

    public OpcionRespuestaResponseDTO() {}

    public Integer getIdRespuesta() {
        return idRespuesta;
    }

    public void setIdRespuesta(Integer idRespuesta) {
        this.idRespuesta = idRespuesta;
    }

    public Integer getValorescala() {
        return valorescala;
    }

    public void setValorescala(Integer valorescala) {
        this.valorescala = valorescala;
    }

    public Integer getIdEmprendimiento() {
        return idEmprendimiento;
    }

    public void setIdEmprendimiento(Integer idEmprendimiento) {
        this.idEmprendimiento = idEmprendimiento;
    }

    public Integer getIdsPregunta() {
        return idsPregunta;
    }

    public void setIdsPregunta(Integer idsPregunta) {
        this.idsPregunta = idsPregunta;
    }

    public List<Integer> getIdsOpciones() {
        return idsOpciones;
    }

    public void setIdsOpciones(List<Integer> idsOpciones) {
        this.idsOpciones = idsOpciones;
    }
}
