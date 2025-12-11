package com.example.eureka.notificacion.infrastructure.dto.request;

import lombok.Data;

import java.util.Map;

@Data
public class NotificacionRequestDTO {

    Integer usuarioId;
    String codigoTipo;
    Map<String, Object> parametros;
    String enlace;
    Integer emprendimientoId;
    Integer solicitudId;


}
