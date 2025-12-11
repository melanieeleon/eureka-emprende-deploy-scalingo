package com.example.eureka.general.application.service;

import com.example.eureka.general.infrastructure.dto.converter.OpcionesPersonaJuridicaDtoConverter;
import com.example.eureka.general.infrastructure.dto.OpcionesPersonaJuridicaDTO;
import com.example.eureka.general.port.out.IOpcionesPersonaJuridicaRepository;
import com.example.eureka.general.port.in.OpcionesPersonaJuridicaService;
import com.example.eureka.general.domain.model.OpcionesPersonaJuridica;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OpcionesPersonaJuridicaServiceImpl implements OpcionesPersonaJuridicaService {


    private final IOpcionesPersonaJuridicaRepository opcionesPersonaJuridicaRepository;

    private final OpcionesPersonaJuridicaDtoConverter opcionesPersonaJuridicaDtoConverter;

    @Override
    public List<OpcionesPersonaJuridicaDTO> listarOpcionesPersonaJuridica() {

        List<OpcionesPersonaJuridica> opcionesPersonaJuridicaList = opcionesPersonaJuridicaRepository.findAll();

        return opcionesPersonaJuridicaList.stream()
                .map(opcionesPersonaJuridicaDtoConverter::convertirUsuarioXEmpresaModelToDto)
                .toList();
    }

    @Override
    public OpcionesPersonaJuridicaDTO obtenerOpcionPersonaJuridicaPorId(Integer id) {

        OpcionesPersonaJuridica opcionesPersonaJuridicaList = opcionesPersonaJuridicaRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró la opción con id: " + id));

        return opcionesPersonaJuridicaDtoConverter.convertirUsuarioXEmpresaModelToDto(opcionesPersonaJuridicaList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OpcionesPersonaJuridicaDTO crearOpcionPersonaJuridica(OpcionesPersonaJuridicaDTO dto) {

        OpcionesPersonaJuridica model = new OpcionesPersonaJuridica();
        if (dto != null) {
            model.setDescripcion(dto.getOpcion());
            model.setEstado(dto.getEstado());
            OpcionesPersonaJuridica saved = opcionesPersonaJuridicaRepository.save(model);
            return opcionesPersonaJuridicaDtoConverter.convertirUsuarioXEmpresaModelToDto(saved);
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OpcionesPersonaJuridicaDTO actualizarOpcionPersonaJuridica(Integer id, OpcionesPersonaJuridicaDTO dto) {

        OpcionesPersonaJuridica model = opcionesPersonaJuridicaRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró la opción con id: " + id));


        if (model != null) {
            model.setDescripcion(dto.getOpcion());
            model.setEstado(dto.getEstado());
            OpcionesPersonaJuridica saved = opcionesPersonaJuridicaRepository.save(model);
            return opcionesPersonaJuridicaDtoConverter.convertirUsuarioXEmpresaModelToDto(saved);
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void eliminarOpcionPersonaJuridica(Integer id) {
        opcionesPersonaJuridicaRepository.deleteById(id);
    }
}
