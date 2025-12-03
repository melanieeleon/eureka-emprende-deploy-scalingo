package com.example.eureka.formulario.application.service;

import com.example.eureka.domain.model.OpcionRespuesta;
import com.example.eureka.domain.model.Opciones;
import com.example.eureka.domain.model.Respuesta;
import com.example.eureka.formulario.infrastructure.dto.response.OpcionResponseDTO;
import com.example.eureka.formulario.infrastructure.dto.response.OpcionRespuestaDTO;
import com.example.eureka.formulario.port.in.OpcionRespuestaService;
import com.example.eureka.formulario.port.out.IOpcionRespuestaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class OpcionRespuestaServiceImpl implements OpcionRespuestaService {

    private final IOpcionRespuestaRepository opcionRespuestaRepository;

    public OpcionRespuestaServiceImpl(IOpcionRespuestaRepository opcionRespuestaRepository) {
        this.opcionRespuestaRepository = opcionRespuestaRepository;
    }


    @Override
    public List<OpcionRespuestaDTO> findAllByRespuesta(Respuesta respuesta) {
        List<OpcionRespuestaDTO> opcionRespuestaDTOs = new ArrayList<>();
        List<OpcionRespuesta> opcionRespuestas = opcionRespuestaRepository.findAllByRespuesta(respuesta);
        if(opcionRespuestas != null) {
            for(OpcionRespuesta opcionRespuesta : opcionRespuestas) {
                OpcionRespuestaDTO opcionRespuestaDTO = new OpcionRespuestaDTO();
                opcionRespuestaDTO.setId(opcionRespuesta.getId());
                opcionRespuestaDTO.setOpciones(opcionRespuesta.getOpciones());
                opcionRespuestaDTO.setRespuesta(opcionRespuesta.getRespuesta());
                opcionRespuestaDTO.setValorescala(opcionRespuesta.getValorescala());
                opcionRespuestaDTOs.add(opcionRespuestaDTO);
            }
            return opcionRespuestaDTOs;
        }
        return List.of();
    }

    @Override
    public List<OpcionRespuestaDTO> findAllByOpciones(Opciones opciones) {
        List<OpcionRespuestaDTO> opcionRespuestaDTOs = new ArrayList<>();
        List<OpcionRespuesta> opcionRespuestas = opcionRespuestaRepository.findAllByOpciones(opciones);
        if(opcionRespuestas != null) {
            for(OpcionRespuesta opcionRespuesta : opcionRespuestas) {
                OpcionRespuestaDTO opcionRespuestaDTO = new OpcionRespuestaDTO();
                opcionRespuestaDTO.setId(opcionRespuesta.getId());
                opcionRespuestaDTO.setOpciones(opcionRespuesta.getOpciones());
                opcionRespuestaDTO.setRespuesta(opcionRespuesta.getRespuesta());
                opcionRespuestaDTO.setValorescala(opcionRespuesta.getValorescala());
                opcionRespuestaDTOs.add(opcionRespuestaDTO);
            }
            return opcionRespuestaDTOs;
        }
        return List.of();
    }

    @Override
    public OpcionRespuestaDTO save(OpcionRespuesta opcionRespuesta) {
        OpcionRespuesta op = opcionRespuestaRepository.save(opcionRespuesta);
        OpcionRespuestaDTO  opcionRespuestaDTO = new OpcionRespuestaDTO();
        opcionRespuestaDTO.setId(op.getId());
        opcionRespuestaDTO.setOpciones(op.getOpciones());
        opcionRespuestaDTO.setRespuesta(op.getRespuesta());
        opcionRespuestaDTO.setValorescala(op.getValorescala());

        return opcionRespuestaDTO;
    }
}
