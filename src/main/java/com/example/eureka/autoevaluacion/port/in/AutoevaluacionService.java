package com.example.eureka.autoevaluacion.port.in;

import com.example.eureka.autoevaluacion.infrastructure.dto.ListadoAutoevaluacionDTO;
import com.example.eureka.autoevaluacion.infrastructure.dto.RespuestaResponseDTO;
import com.example.eureka.entrepreneurship.domain.model.Emprendimientos;
import com.example.eureka.autoevaluacion.domain.model.Respuesta;
import com.example.eureka.formulario.infrastructure.dto.response.OpcionRespuestaDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AutoevaluacionService {

    Respuesta findById(Long idRespuesta);

 /*   Page<EmprendimientoInfo> obtenerEmprendimientos(Pageable pageable);

    boolean existsByEmprendimientos(Emprendimientos emprendimientos);

    List<RespuestaFormularioDTO> obtenerRespuestasPorEmprendimiento(Long idEmprendimiento);*/

    Respuesta saveRespuesta(RespuestaResponseDTO respuesta);

    Page<ListadoAutoevaluacionDTO> listarAutoevaluaciones(Pageable pageable);
    Page<OpcionRespuestaDTO> obtenerDetalleAutoevaluacion(Long idAutoevaluacion, Pageable pageable);


}
