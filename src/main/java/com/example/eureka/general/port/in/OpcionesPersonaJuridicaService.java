package com.example.eureka.general.port.in;

import com.example.eureka.general.infrastructure.dto.OpcionesPersonaJuridicaDTO;

import java.util.List;

public interface OpcionesPersonaJuridicaService {

    List<OpcionesPersonaJuridicaDTO> listarOpcionesPersonaJuridica();
    OpcionesPersonaJuridicaDTO obtenerOpcionPersonaJuridicaPorId(Integer id);
    OpcionesPersonaJuridicaDTO crearOpcionPersonaJuridica(OpcionesPersonaJuridicaDTO dto);
    OpcionesPersonaJuridicaDTO actualizarOpcionPersonaJuridica(Integer id, OpcionesPersonaJuridicaDTO dto);
    void eliminarOpcionPersonaJuridica(Integer id);

}
