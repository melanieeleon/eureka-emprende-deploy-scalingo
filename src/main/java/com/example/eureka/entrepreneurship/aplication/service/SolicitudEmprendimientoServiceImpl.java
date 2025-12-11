package com.example.eureka.entrepreneurship.aplication.service;

import com.example.eureka.entrepreneurship.infrastructure.dto.shared.SolicitudEmprendimientoDTO;
import com.example.eureka.entrepreneurship.port.in.SolicitudEmprendimientoService;
import com.example.eureka.entrepreneurship.port.out.ISolicitudesEmprendimientoRepository;
import com.example.eureka.entrepreneurship.domain.model.SolicitudEmprendimiento;
import com.example.eureka.entrepreneurship.domain.model.Emprendimientos;
import com.example.eureka.auth.domain.Usuarios;
import com.example.eureka.entrepreneurship.port.out.IEmprendimientosRepository;
import com.example.eureka.auth.port.out.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SolicitudEmprendimientoServiceImpl implements SolicitudEmprendimientoService {
    private final ISolicitudesEmprendimientoRepository repository;
    private final IUserRepository usuarioRepository;
    private final IEmprendimientosRepository emprendimientoRepository;

    @Override
    public SolicitudEmprendimientoDTO crear(SolicitudEmprendimientoDTO dto) {
        Optional<Usuarios> usuarioOpt = usuarioRepository.findById(dto.getUsuarioId());
        Optional<Usuarios> adminOpt = dto.getUsuarioAdministradorId() != null ? usuarioRepository.findById(dto.getUsuarioAdministradorId()) : Optional.empty();
        Optional<Emprendimientos> emprendimientoOpt = emprendimientoRepository.findById(dto.getEmprendimientoId());
        if (usuarioOpt.isEmpty() || emprendimientoOpt.isEmpty() || (dto.getUsuarioAdministradorId() != null && adminOpt.isEmpty())) {
            return null;
        }
        SolicitudEmprendimiento entity = new SolicitudEmprendimiento();
        entity.setEstado(dto.getEstado());
        entity.setObservaciones(dto.getObservaciones());
        entity.setFechaSolicitud(dto.getFechaSolicitud());
        entity.setFechaRespuesta(dto.getFechaRespuesta());
        entity.setEmprendimiento(emprendimientoOpt.get());
        entity.setUsuario(usuarioOpt.get());
        entity.setUsuarioAdministrador(dto.getUsuarioAdministradorId() != null ? adminOpt.get() : null);
        entity = repository.save(entity);
        return toDTO(entity);
    }

    @Override
    public SolicitudEmprendimientoDTO actualizar(Integer id, SolicitudEmprendimientoDTO dto) {
        Optional<SolicitudEmprendimiento> opt = repository.findById(id);
        if (opt.isEmpty()) return null;
        SolicitudEmprendimiento entity = opt.get();
        Optional<Usuarios> usuarioOpt = usuarioRepository.findById(dto.getUsuarioId());
        Optional<Emprendimientos> emprendimientoOpt = emprendimientoRepository.findById(dto.getEmprendimientoId());
        if (usuarioOpt.isEmpty() || emprendimientoOpt.isEmpty()) {
            return null;
        }
        entity.setEstado(dto.getEstado());
        entity.setObservaciones(dto.getObservaciones());
        entity.setFechaSolicitud(dto.getFechaSolicitud());
        entity.setFechaRespuesta(dto.getFechaRespuesta());
        entity.setEmprendimiento(emprendimientoOpt.get());
        entity.setUsuario(usuarioOpt.get());
        // No modificar el usuarioAdministrador (revisor)
        entity = repository.save(entity);
        return toDTO(entity);
    }

    @Override
    public void eliminar(Integer id) {
        repository.deleteById(id);
    }

    @Override
    public SolicitudEmprendimientoDTO obtenerPorId(Integer id) {
        Optional<SolicitudEmprendimiento> opt = repository.findById(id);
        return opt.map(this::toDTO).orElse(null);
    }

    @Override
    public List<SolicitudEmprendimientoDTO> obtenerTodos() {
        return repository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    private SolicitudEmprendimientoDTO toDTO(SolicitudEmprendimiento entity) {
        SolicitudEmprendimientoDTO dto = new SolicitudEmprendimientoDTO();
        dto.setId(entity.getId());
        dto.setEstado(entity.getEstado());
        dto.setObservaciones(entity.getObservaciones());
        dto.setFechaSolicitud(entity.getFechaSolicitud());
        dto.setFechaRespuesta(entity.getFechaRespuesta());
        dto.setEmprendimientoId(entity.getEmprendimiento() != null ? entity.getEmprendimiento().getId() : null);
        dto.setUsuarioId(entity.getUsuario() != null ? entity.getUsuario().getId() : null);
        dto.setUsuarioAdministradorId(entity.getUsuarioAdministrador() != null ? entity.getUsuarioAdministrador().getId() : null);
        return dto;
    }
}
