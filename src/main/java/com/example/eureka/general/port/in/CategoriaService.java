package com.example.eureka.general.port.in;

import com.example.eureka.general.infrastructure.dto.CategoriaRequestDTO;
import com.example.eureka.general.infrastructure.dto.CategoriasDTO;

import java.util.List;

public interface CategoriaService {
    List<CategoriasDTO> listarCategoaria();
    CategoriasDTO obtenerCategoriaPorId(Integer id);
    CategoriasDTO crearCategoria(CategoriaRequestDTO dto);
    CategoriasDTO actualizarCategoaria(Integer id, CategoriaRequestDTO dto);
    void eliminarCategoria(Integer id);
}