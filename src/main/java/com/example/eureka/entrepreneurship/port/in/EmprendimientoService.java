package com.example.eureka.entrepreneurship.port.in;

import com.example.eureka.entrepreneurship.domain.model.Emprendimientos;
import com.example.eureka.entrepreneurship.domain.model.SolicitudAprobacion;
import com.example.eureka.auth.domain.Usuarios;
import com.example.eureka.entrepreneurship.infrastructure.dto.publico.EmprendimientoListaPublicoDTO;
import com.example.eureka.entrepreneurship.infrastructure.dto.publico.MiniEmprendimientoDTO;
import com.example.eureka.entrepreneurship.infrastructure.dto.request.EmprendimientoRequestDTO;
import com.example.eureka.entrepreneurship.infrastructure.dto.shared.EmprendimientoDTO;
import com.example.eureka.entrepreneurship.infrastructure.dto.shared.EmprendimientoPorCategoriaDTO;
import com.example.eureka.entrepreneurship.infrastructure.dto.shared.EmprendimientoResponseDTO;
import com.example.eureka.entrepreneurship.infrastructure.dto.shared.VistaEmprendedorDTO;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EmprendimientoService {

    Integer estructuraEmprendimiento(EmprendimientoRequestDTO emprendimientoRequestDTO) throws Exception;

    List<MiniEmprendimientoDTO> obtenerEmprendimientos();

    List<EmprendimientoListaPublicoDTO> obtenesListaDeEmprendimientos(Usuarios usuario);

    EmprendimientoPorCategoriaDTO obtenerEmprendimientosPorCategoria(Integer categoriaId);

    EmprendimientoResponseDTO obtenerEmprendimientoPorId(Integer id);

    Emprendimientos crearBorradorEmprendimiento(@Valid EmprendimientoDTO emprendimientoDTO, Usuarios usuario);

    EmprendimientoResponseDTO obtenerEmprendimientoCompletoPorId(Integer id);

    EmprendimientoResponseDTO actualizarEmprendimiento(Integer id, EmprendimientoRequestDTO emprendimientoRequestDTO) throws Exception;

    // Nuevos métodos para sistema de aprobación
    SolicitudAprobacion enviarParaAprobacion(Integer emprendimientoId, Usuarios usuario);
    VistaEmprendedorDTO obtenerVistaEmprendedor(Integer emprendimientoId);

    List<EmprendimientoResponseDTO> obtenerEmprendimientosPorUsuario(Usuarios usuario);

    /**
     * Obtener emprendimientos filtrados por nombre, tipo, categoría y ciudad
     * @param nombre
     * @param tipo
     * @param categoria
     * @param ciudad
     * @return
     */
   // List<EmprendimientoResponseDTO> obtenerEmprendimientosFiltrado(String nombre, String tipo, String categoria, String ciudad);

    /**
     * Obtener emprendimientos filtrados por nombre, tipo, categoría y ciudad, paginado
     */
    Page<EmprendimientoResponseDTO> obtenerEmprendimientosFiltrado(String nombre, String tipo, String categoria, String ciudad, Pageable pageable);

    void inactivarEmprendimiento(Integer id) throws Exception;

    void activarEmprendimiento(Integer id) throws Exception;
}
