package com.example.eureka.general.port.in;

import com.example.eureka.general.infrastructure.dto.DeclaracionesFinalesDTO;
import java.util.List;

public interface DeclaracionesFinalesService {
    List<DeclaracionesFinalesDTO> listar();
    DeclaracionesFinalesDTO obtenerPorId(Integer id);
    DeclaracionesFinalesDTO guardar(DeclaracionesFinalesDTO dto);
    DeclaracionesFinalesDTO actualizar(Integer id, DeclaracionesFinalesDTO dto);
    void eliminar(Integer id);
}
