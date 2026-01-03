package com.example.eureka.formulario.application.service;

import com.example.eureka.formulario.domain.model.Formulario;
import com.example.eureka.formulario.domain.model.FormularioPregunta;
import com.example.eureka.formulario.infrastructure.dto.response.FormularioResponseDTO;
import com.example.eureka.formulario.infrastructure.dto.response.OpcionResponseDTO;
import com.example.eureka.formulario.infrastructure.dto.response.PreguntaResponseDTO;
import com.example.eureka.formulario.port.in.FormularioService;
import com.example.eureka.formulario.port.out.IFormularioRepository;
import com.example.eureka.shared.exception.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class FormularioServiceImpl implements FormularioService {

    private final IFormularioRepository formularioRepository;

    public FormularioServiceImpl(IFormularioRepository formularioRepository) {
        this.formularioRepository = formularioRepository;
    }

    @Override
    public FormularioResponseDTO getFormularioByTipo(String tipoFormulario) {
        Formulario formulario = formularioRepository.findByTipoFormularioNombre(tipoFormulario)
                .orElseThrow(() -> new BusinessException("Formulario no encontrado para el tipo: " + tipoFormulario));

        return mapToDTO(formulario);
    }


    private FormularioResponseDTO mapToDTO(Formulario formulario) {
        FormularioResponseDTO dto = new FormularioResponseDTO();
        dto.setIdFormulario(formulario.getIdFormulario());
        dto.setNombre(formulario.getNombre());
        dto.setTipoFormulario(formulario.getTipoFormulario().getNombre());

        // Convertir Set a List y ordenar
        List<FormularioPregunta> preguntasOrdenadas = new ArrayList<>(formulario.getFormularioPreguntas());
        preguntasOrdenadas.sort(Comparator.comparing(FormularioPregunta::getOrden));

        List<PreguntaResponseDTO> preguntas = preguntasOrdenadas.stream()
                .map(fp -> {
                    PreguntaResponseDTO preguntaDTO = new PreguntaResponseDTO();
                    preguntaDTO.setIdPregunta(fp.getPregunta().getIdPregunta());
                    preguntaDTO.setPregunta(fp.getPregunta().getPregunta());
                    preguntaDTO.setTipo(fp.getPregunta().getTipo());
                    preguntaDTO.setNumeroRespuestas(fp.getPregunta().getNumeroRespuestas());
                    preguntaDTO.setObligatoria(fp.getObligatoria());
                    preguntaDTO.setOrden(fp.getOrden());

                    // Inicializar opciones dentro de @Transactional
                    if (fp.getPregunta().getOpciones() != null && !fp.getPregunta().getOpciones().isEmpty()) {
                        List<OpcionResponseDTO> opciones = fp.getPregunta().getOpciones().stream()
                                .filter(o -> "ACTIVO".equals(o.getEstado()))
                                .map(o -> new OpcionResponseDTO(o.getIdOpcion(), o.getOpcion()))
                                .collect(Collectors.toList());
                        preguntaDTO.setOpciones(opciones);
                    }

                    return preguntaDTO;
                })
                .collect(Collectors.toList());

        dto.setPreguntas(preguntas);
        return dto;
    }
}