package com.example.eureka.general.infrastructure.dto.converter;

import com.example.eureka.general.infrastructure.dto.OpcionesPersonaJuridicaDTO;
import com.example.eureka.general.domain.model.OpcionesPersonaJuridica;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OpcionesPersonaJuridicaDtoConverter {

    public OpcionesPersonaJuridicaDTO convertirUsuarioXEmpresaModelToDto(OpcionesPersonaJuridica model) {
        if (model != null) {
            OpcionesPersonaJuridicaDTO dto = new OpcionesPersonaJuridicaDTO();
            dto.setId(model.getId());
            dto.setOpcion(model.getDescripcion());
            dto.setEstado(model.getEstado());

            return dto;
        }
        return null;
    }


}
