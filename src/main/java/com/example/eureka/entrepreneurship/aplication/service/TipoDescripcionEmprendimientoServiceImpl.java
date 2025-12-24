package com.example.eureka.entrepreneurship.aplication.service;

import com.example.eureka.entrepreneurship.infrastructure.dto.shared.TipoDescripcionEmprendimientoDTO;
import com.example.eureka.entrepreneurship.infrastructure.dto.shared.EmprendimientoResponseDTO;
import com.example.eureka.entrepreneurship.infrastructure.mappers.EmprendimientoMapper;
import com.example.eureka.entrepreneurship.port.out.IEmprendimientosDescripcionRepository;
import com.example.eureka.entrepreneurship.port.out.IEmprendimientosRepository;
import com.example.eureka.entrepreneurship.port.in.TipoDescripcionEmprendimientoService;
import com.example.eureka.entrepreneurship.domain.model.Emprendimientos;
import com.example.eureka.entrepreneurship.domain.model.DescripcionEmprendimiento;
import com.example.eureka.general.domain.model.Descripciones;
import com.example.eureka.general.port.out.IDescripcionesRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TipoDescripcionEmprendimientoServiceImpl implements TipoDescripcionEmprendimientoService {

    private final IEmprendimientosDescripcionRepository repository;
    private final IEmprendimientosRepository emprendimientosRepository;
    private final IDescripcionesRepository   descripcionesRepository;

    @Override
    public List<TipoDescripcionEmprendimientoDTO> listar() {
        return repository.findAll()
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    public TipoDescripcionEmprendimientoDTO obtenerPorId(Integer id) {
        DescripcionEmprendimiento entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró el registro con id: " + id));
        return convertirADTO(entity);
    }

    @Override
    public TipoDescripcionEmprendimientoDTO guardar(TipoDescripcionEmprendimientoDTO dto) {
        DescripcionEmprendimiento entity = convertirAEntidad(dto);

        // 🔹 Asociar emprendimiento si se envía
        if (dto.getEmprendimiento() != null && dto.getEmprendimiento().getId() != null) {
            Emprendimientos emprendimiento = emprendimientosRepository.findById(dto.getEmprendimiento().getId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Emprendimiento no encontrado con id: " + dto.getEmprendimiento().getId()));
            entity.setEmprendimiento(emprendimiento);
        }

        DescripcionEmprendimiento guardado = repository.save(entity);
        return convertirADTO(guardado);
    }

    @Override
    public TipoDescripcionEmprendimientoDTO actualizar(Integer id, TipoDescripcionEmprendimientoDTO dto) {
        DescripcionEmprendimiento existente = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró el registro con id: " + id));

        Descripciones des = descripcionesRepository.findById(dto.getDescripcionId()).orElseThrow(() -> new EntityNotFoundException());
        Emprendimientos emp = emprendimientosRepository.findById(dto.getEmprendimiento().getId()).orElseThrow(() -> new EntityNotFoundException());
        existente.setDescripciones(des);
        existente.setEmprendimiento(emp);
        existente.setRespuesta(dto.getRespuesta());

        if (dto.getEmprendimiento() != null && dto.getEmprendimiento().getId() != null) {
            Emprendimientos emprendimiento = emprendimientosRepository.findById(dto.getEmprendimiento().getId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Emprendimiento no encontrado con id: " + dto.getEmprendimiento().getId()));
            existente.setEmprendimiento(emprendimiento);
        }

        DescripcionEmprendimiento actualizado = repository.save(existente);
        return convertirADTO(actualizado);
    }

    @Override
    public void eliminar(Integer id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("No existe registro con id: " + id);
        }
        repository.deleteById(id);
    }

    private TipoDescripcionEmprendimientoDTO convertirADTO(DescripcionEmprendimiento entity) {
        TipoDescripcionEmprendimientoDTO dto = new TipoDescripcionEmprendimientoDTO();
        dto.setId(entity.getId());
        dto.setDescripcionId(entity.getDescripciones().getId());
        dto.setRespuesta(entity.getRespuesta());

        if (entity.getEmprendimiento() != null) {
            EmprendimientoResponseDTO eDto = EmprendimientoMapper.toResponseDTO(entity.getEmprendimiento());
            dto.setEmprendimiento(eDto);
        }

        return dto;
    }

    private DescripcionEmprendimiento convertirAEntidad(TipoDescripcionEmprendimientoDTO dto) {
        DescripcionEmprendimiento entity = new DescripcionEmprendimiento();

        if (dto.getId() != null) {
            entity.setId(dto.getId());
        }
        Descripciones des = descripcionesRepository.findById(dto.getDescripcionId()).orElse(null);
        Emprendimientos emp = emprendimientosRepository.findById(dto.getEmprendimiento().getId()).orElse(null);
        entity.setDescripciones(des);
        entity.setEmprendimiento(emp);
        entity.setRespuesta(dto.getRespuesta());

        return entity;
    }
}
