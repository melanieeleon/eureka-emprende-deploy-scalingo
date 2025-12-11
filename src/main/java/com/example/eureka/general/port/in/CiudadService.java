package com.example.eureka.general.port.in;

import com.example.eureka.general.infrastructure.dto.CiudadDTO;

import java.util.List;

public interface CiudadService {
    List<CiudadDTO> listar();
    List<CiudadDTO> obtenerCiudadPorProvinciaId(Integer id);
    CiudadDTO obtenerPorId(Integer id);
    CiudadDTO guardar(CiudadDTO dto);
    CiudadDTO actualizar(Integer id, CiudadDTO dto);
    void eliminar(Integer id);
}