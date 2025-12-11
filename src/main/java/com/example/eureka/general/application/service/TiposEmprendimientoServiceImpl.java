package com.example.eureka.general.application.service;

import com.example.eureka.general.infrastructure.dto.TipoEmprendimientoDTO;
import com.example.eureka.general.port.out.ITipoEmprendimientoRepository;
import com.example.eureka.general.port.in.TiposEmprendimientoService;
import com.example.eureka.general.domain.model.TiposEmprendimientos;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TiposEmprendimientoServiceImpl implements TiposEmprendimientoService {

    private final ITipoEmprendimientoRepository tipoRepo;

    @Override
    public List<TipoEmprendimientoDTO> listar() {
        return tipoRepo.findAll()
                .stream()
                .map(entity -> {
                    TipoEmprendimientoDTO dto = new TipoEmprendimientoDTO();
                    dto.setId(entity.getId());
                    dto.setTipo(entity.getTipo());
                    dto.setSubTipo(entity.getSubTipo());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public TipoEmprendimientoDTO obtenerPorId(Integer id) {
        return tipoRepo.findById(id)
                .map(entity -> {
                    TipoEmprendimientoDTO dto = new TipoEmprendimientoDTO();
                    dto.setId(entity.getId());
                    dto.setTipo(entity.getTipo());
                    dto.setSubTipo(entity.getSubTipo());
                    return dto;
                })
                .orElse(null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TipoEmprendimientoDTO guardar(TipoEmprendimientoDTO dto) {
        TiposEmprendimientos entity = new TiposEmprendimientos();
        entity.setTipo(dto.getTipo());
        entity.setSubTipo(dto.getSubTipo());

        TiposEmprendimientos saved = tipoRepo.save(entity);

        TipoEmprendimientoDTO response = new TipoEmprendimientoDTO();
        response.setTipo(saved.getTipo());
        response.setSubTipo(saved.getSubTipo());
        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TipoEmprendimientoDTO actualizar(Integer id, TipoEmprendimientoDTO dto) {
        return tipoRepo.findById(id)
                .map(entity -> {
                    entity.setTipo(dto.getTipo());
                    entity.setSubTipo(dto.getSubTipo());
                    TiposEmprendimientos updated = tipoRepo.save(entity);

                    TipoEmprendimientoDTO response = new TipoEmprendimientoDTO();
                    response.setTipo(updated.getTipo());
                    response.setSubTipo(updated.getSubTipo());
                    return response;
                })
                .orElse(null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void eliminar(Integer id) {
        tipoRepo.deleteById(id);
    }
}
