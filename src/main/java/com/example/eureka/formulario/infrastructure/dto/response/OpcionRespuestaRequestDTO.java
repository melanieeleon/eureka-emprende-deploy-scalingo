package com.example.eureka.formulario.infrastructure.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpcionRespuestaRequestDTO {
    private Integer idEmprendimiento;
    private Integer idRespuesta;
    private Integer idRespuestaValoracion;
    private Integer idsPregunta;
    private List<Integer> idsOpciones;
    private Integer valorescala;
    private String tipoFormulario;  // NUEVO

}
