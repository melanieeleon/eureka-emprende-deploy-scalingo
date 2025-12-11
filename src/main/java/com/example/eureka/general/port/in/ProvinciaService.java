package com.example.eureka.general.port.in;

import com.example.eureka.general.infrastructure.dto.ProvinciaDTO;
import java.util.List;

public interface ProvinciaService {

    Integer crearProvincia(ProvinciaDTO provinciaDTO);

    Integer actualizarProvincia(Integer id, ProvinciaDTO provinciaDTO);

    void eliminarProvincia(Integer id);

    List<ProvinciaDTO> obtenerProvincias();

    ProvinciaDTO obtenerProvinciaPorId(Integer id);
}
