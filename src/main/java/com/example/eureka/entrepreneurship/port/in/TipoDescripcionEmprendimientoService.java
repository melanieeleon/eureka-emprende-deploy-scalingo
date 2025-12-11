package com.example.eureka.entrepreneurship.port.in;

import com.example.eureka.entrepreneurship.infrastructure.dto.shared.TipoDescripcionEmprendimientoDTO;

import java.util.List;

public interface TipoDescripcionEmprendimientoService {
    List<TipoDescripcionEmprendimientoDTO> listar();
    TipoDescripcionEmprendimientoDTO obtenerPorId(Integer id);
    TipoDescripcionEmprendimientoDTO guardar(TipoDescripcionEmprendimientoDTO dto);
    TipoDescripcionEmprendimientoDTO actualizar(Integer id, TipoDescripcionEmprendimientoDTO dto);
    void eliminar(Integer id);
}
