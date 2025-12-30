package com.example.eureka.autoevaluacion.application.service;

import com.example.eureka.autoevaluacion.infrastructure.dto.*;
import com.example.eureka.entrepreneurship.domain.model.Emprendimientos;
import com.example.eureka.autoevaluacion.domain.model.Respuesta;
import com.example.eureka.autoevaluacion.port.out.IAutoevaluacionRepository;
import com.example.eureka.autoevaluacion.port.in.AutoevaluacionService;
import com.example.eureka.entrepreneurship.domain.model.OpcionRespuesta;
import com.example.eureka.entrepreneurship.port.out.IEmprendimientosRepository;
import com.example.eureka.formulario.domain.model.Formulario;
import com.example.eureka.formulario.infrastructure.dto.response.OpcionRespuestaDTO;
import com.example.eureka.formulario.port.in.OpcionRespuestaService;
import com.example.eureka.formulario.port.out.IFormularioRepository;
import com.example.eureka.formulario.port.out.IOpcionRespuestaRepository;
import com.example.eureka.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AutoevaluacionServiceImpl implements AutoevaluacionService {

    private final IAutoevaluacionRepository valoracionRepository;
    private final IFormularioRepository formularioRepository;
    private final IEmprendimientosRepository  emprendimientosRepository;
    private final OpcionRespuestaService opcionRespuestaService;
    private final IOpcionRespuestaRepository opcionRespuestaRepository;// inyéctalo


    public Page<ListadoAutoevaluacionDTO> listarAutoevaluaciones(Pageable pageable) {
        Page<Respuesta> page = valoracionRepository.findAllByEsAutoEvaluacionTrue(pageable);

        return page.map(r -> {
            ListadoAutoevaluacionDTO dto = new ListadoAutoevaluacionDTO();
            dto.setIdAutoevaluacion(r.getId());
            dto.setIdValoracionOrigen(r.getRespuesta() != null ? r.getRespuesta().getId() : null);
            dto.setEsAutoevaluacion(r.getEsAutoEvaluacion());
            dto.setFechaRespuesta(r.getFechaRespuesta());
            dto.setIdFormulario(r.getFormulario().getIdFormulario().intValue());
            dto.setFormulario(r.getFormulario().getNombre());
            dto.setIdEmprendimiento(r.getEmprendimientos().getId());
            dto.setEmprendimiento(r.getEmprendimientos().getNombreComercial());

            // NUEVO: Agregar datos de la valoración origen
            if (r.getRespuesta() != null) {
                ValoracionResumenDTO valoracion = new ValoracionResumenDTO();
                valoracion.setIdValoracion(r.getRespuesta().getId());
                valoracion.setFechaValoracion(r.getRespuesta().getFechaRespuesta());
                valoracion.setTipoFormulario(r.getRespuesta().getFormulario().getTipoFormulario().getNombre());

                // Calcular promedio de la valoración
                Double promedio = calcularPromedioValoracion(r.getRespuesta().getId());
                valoracion.setPromedio(promedio);

                dto.setValoracionOrigen(valoracion);
            }

            return dto;
        });
    }

    // Método auxiliar para calcular el promedio
    private Double calcularPromedioValoracion(Integer idRespuestaValoracion) {
        // Busca todas las OpcionRespuesta de esa valoración y calcula el promedio
        List<OpcionRespuesta> opciones = opcionRespuestaRepository
                .findByRespuestaId(idRespuestaValoracion);

        if (opciones.isEmpty()) {
            return 0.0;
        }

        Double suma = opciones.stream()
                .filter(o -> o.getValorescala() != null)
                .mapToDouble(OpcionRespuesta::getValorescala)
                .sum();

        long cantidad = opciones.stream()
                .filter(o -> o.getValorescala() != null)
                .count();

        return cantidad > 0 ? suma / cantidad : 0.0;
    }


    public Page<OpcionRespuestaDTO> obtenerDetalleAutoevaluacion(Long idAutoevaluacion, Pageable pageable) {
        Respuesta autoeval = valoracionRepository.findById(idAutoevaluacion.intValue())
                .orElseThrow(() -> new BusinessException("Autoevaluación no encontrada"));

        return opcionRespuestaService.findAllByRespuesta(autoeval, pageable);
    }



    @Override
    public Respuesta findById(Long idRespuesta) {
        return valoracionRepository.findById(idRespuesta.intValue()).orElse(null);
    }
/**
    @Override
    public Page<EmprendimientoInfo> obtenerEmprendimientos(Pageable pageable) {
        return valoracionRepository.obtenerEmprendimientos(pageable);
    }

    @Override
    public boolean existsByEmprendimientos(Emprendimientos emprendimientos) {
        return valoracionRepository.existsByEmprendimientos(emprendimientos);
    }

    @Override
    public List<RespuestaFormularioDTO> obtenerRespuestasPorEmprendimiento(Long idEmprendimiento) {
        return valoracionRepository.obtenerRespuestasPorEmprendimiento(idEmprendimiento);
    }
*/
    @Override
    public Respuesta saveRespuesta(RespuestaResponseDTO respuesta){
        Formulario fm = formularioRepository.findById(respuesta.getIdFormulario().longValue()).orElse(null);
        Respuesta idR = valoracionRepository.findById(respuesta.getIdRespuesta().intValue()).orElse(null);
        Emprendimientos emp = emprendimientosRepository.findById(respuesta.getIdEmprendimiento().intValue()).orElse(null);

        Respuesta data = new Respuesta();
        data.setRespuesta(idR);
        data.setEmprendimientos(emp);
        data.setFormulario(fm);
        data.setFechaRespuesta(respuesta.getFechaRespuesta());
        data.setEsAutoEvaluacion(respuesta.getEsAutoevaluacion());
        return valoracionRepository.save(data);
    }
}
