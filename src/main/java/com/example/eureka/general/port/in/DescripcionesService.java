package com.example.eureka.general.port.in;

import com.example.eureka.general.infrastructure.dto.DescripcionesDTO;

import java.util.List;

public interface DescripcionesService {
    List<DescripcionesDTO> listar();
    DescripcionesDTO obtenerPorId(Integer id);
    DescripcionesDTO guardar(DescripcionesDTO dto);
    DescripcionesDTO actualizar(Integer id, DescripcionesDTO dto);
    void eliminar(Integer id);
}
