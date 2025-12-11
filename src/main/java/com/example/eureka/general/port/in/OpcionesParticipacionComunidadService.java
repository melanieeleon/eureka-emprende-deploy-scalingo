package com.example.eureka.general.port.in;

import com.example.eureka.general.infrastructure.dto.OpcionesParticipacionComunidadDTO;

import java.util.List;

public interface OpcionesParticipacionComunidadService {
    List<OpcionesParticipacionComunidadDTO> listar();
    OpcionesParticipacionComunidadDTO obtenerPorId(Integer id);
    OpcionesParticipacionComunidadDTO guardar(OpcionesParticipacionComunidadDTO dto);
    OpcionesParticipacionComunidadDTO actualizar(Integer id, OpcionesParticipacionComunidadDTO dto);
    void eliminar(Integer id);
}
