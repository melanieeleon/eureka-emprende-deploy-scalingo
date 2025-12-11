package com.example.eureka.entrepreneurship.aplication.service;

import com.example.eureka.entrepreneurship.infrastructure.dto.shared.TipoDescripcionEmprendimientoDTO;
import com.example.eureka.entrepreneurship.infrastructure.dto.shared.EmprendimientoResponseDTO;
import com.example.eureka.entrepreneurship.infrastructure.mappers.EmprendimientoMapper;
import com.example.eureka.entrepreneurship.port.out.IEmprendimientosDescripcionRepository;
import com.example.eureka.entrepreneurship.port.out.IEmprendimientosRepository;
import com.example.eureka.entrepreneurship.port.in.TipoDescripcionEmprendimientoService;
import com.example.eureka.entrepreneurship.domain.model.Emprendimientos;
import com.example.eureka.entrepreneurship.domain.model.TiposDescripcionEmprendimiento;
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

    @Override
    public List<TipoDescripcionEmprendimientoDTO> listar() {
        return repository.findAll()
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    public TipoDescripcionEmprendimientoDTO obtenerPorId(Integer id) {
        TiposDescripcionEmprendimiento entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró el registro con id: " + id));
        return convertirADTO(entity);
    }

    @Override
    public TipoDescripcionEmprendimientoDTO guardar(TipoDescripcionEmprendimientoDTO dto) {
        TiposDescripcionEmprendimiento entity = convertirAEntidad(dto);

        // 🔹 Asociar emprendimiento si se envía
        if (dto.getEmprendimiento() != null && dto.getEmprendimiento().getId() != null) {
            Emprendimientos emprendimiento = emprendimientosRepository.findById(dto.getEmprendimiento().getId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Emprendimiento no encontrado con id: " + dto.getEmprendimiento().getId()));
            entity.setEmprendimiento(emprendimiento);
        }

        TiposDescripcionEmprendimiento guardado = repository.save(entity);
        return convertirADTO(guardado);
    }

    @Override
    public TipoDescripcionEmprendimientoDTO actualizar(Integer id, TipoDescripcionEmprendimientoDTO dto) {
        TiposDescripcionEmprendimiento existente = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró el registro con id: " + id));

        existente.setTipoDescripcion(dto.getTipoDescripcion());
        existente.setDescripcion(dto.getDescripcion());
        existente.setMaxCaracteres(dto.getMaxCaracteres());
        existente.setObligatorio(dto.getObligatorio());

        if (dto.getEmprendimiento() != null && dto.getEmprendimiento().getId() != null) {
            Emprendimientos emprendimiento = emprendimientosRepository.findById(dto.getEmprendimiento().getId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Emprendimiento no encontrado con id: " + dto.getEmprendimiento().getId()));
            existente.setEmprendimiento(emprendimiento);
        }

        TiposDescripcionEmprendimiento actualizado = repository.save(existente);
        return convertirADTO(actualizado);
    }

    @Override
    public void eliminar(Integer id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("No existe registro con id: " + id);
        }
        repository.deleteById(id);
    }

    private TipoDescripcionEmprendimientoDTO convertirADTO(TiposDescripcionEmprendimiento entity) {
        TipoDescripcionEmprendimientoDTO dto = new TipoDescripcionEmprendimientoDTO();
        dto.setId(entity.getId());
        dto.setTipoDescripcion(entity.getTipoDescripcion());
        dto.setDescripcion(entity.getDescripcion());
        dto.setMaxCaracteres(entity.getMaxCaracteres());
        dto.setObligatorio(entity.getObligatorio());

        if (entity.getEmprendimiento() != null) {
            EmprendimientoResponseDTO eDto = EmprendimientoMapper.toResponseDTO(entity.getEmprendimiento());
            dto.setEmprendimiento(eDto);
        }

        return dto;
    }

    private TiposDescripcionEmprendimiento convertirAEntidad(TipoDescripcionEmprendimientoDTO dto) {
        TiposDescripcionEmprendimiento entity = new TiposDescripcionEmprendimiento();

        if (dto.getId() != null) {
            entity.setId(dto.getId());
        }

        entity.setTipoDescripcion(dto.getTipoDescripcion());
        entity.setDescripcion(dto.getDescripcion());
        entity.setMaxCaracteres(dto.getMaxCaracteres());
        entity.setObligatorio(dto.getObligatorio());

        return entity;
    }
}
