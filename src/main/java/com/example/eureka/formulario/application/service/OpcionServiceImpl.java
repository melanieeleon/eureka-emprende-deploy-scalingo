package com.example.eureka.formulario.application.service;

import com.example.eureka.formulario.domain.model.Opciones;
import com.example.eureka.formulario.infrastructure.dto.response.OpcionResponseDTO;
import com.example.eureka.formulario.port.in.OpcionService;
import com.example.eureka.formulario.port.out.IOpcionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly=true)
public class OpcionServiceImpl implements OpcionService {

    private final IOpcionRepository opcionRepository;

    public OpcionServiceImpl(IOpcionRepository opcionRepository) {
        this.opcionRepository = opcionRepository;
    }


    @Override
    public Opciones findById(Long id) {
        return opcionRepository.findById(id);
    }

    @Override
    public OpcionResponseDTO save(Opciones opcion) {
        Opciones op = opcionRepository.save(opcion);
        OpcionResponseDTO opcionResponseDTO = new OpcionResponseDTO();
        opcionResponseDTO.setIdOpcion(op.getIdOpcion());
        opcionResponseDTO.setOpcion(op.getOpcion());
        return opcionResponseDTO;
    }
}
