package com.example.eureka.general.application.service;

import com.example.eureka.general.infrastructure.dto.ProvinciaDTO;
import com.example.eureka.general.port.out.IProvinciaRepository;
import com.example.eureka.general.port.in.ProvinciaService;
import com.example.eureka.general.domain.model.Provincias;
import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProvinciaServiceImpl implements ProvinciaService {

    private final IProvinciaRepository provinciaRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer crearProvincia(ProvinciaDTO provinciaDTO) {
        if (provinciaDTO == null) {
            throw new IllegalArgumentException("El DTO de provincia no puede ser nulo");
        }

        Provincias provincia = new Provincias();
        provincia.setNombre(provinciaDTO.getNombre());
        provincia.setActivo(provinciaDTO.getActivo() != null ? provinciaDTO.getActivo() : true);

        Provincias guardada = provinciaRepository.save(provincia);
        return guardada.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer actualizarProvincia(Integer id, ProvinciaDTO provinciaDTO) {
        Provincias provincia = provinciaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Provincia no encontrada con ID: " + id));

        provincia.setNombre(provinciaDTO.getNombre());
        provincia.setActivo(provinciaDTO.getActivo());

        provinciaRepository.save(provincia);
        return id;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void eliminarProvincia(Integer id) {
        if (!provinciaRepository.existsById(id)) {
            throw new EntityNotFoundException("No existe una provincia con el ID: " + id);
        }
        provinciaRepository.deleteById(id);
    }

    @Override
    public List<ProvinciaDTO> obtenerProvincias() {
        return provinciaRepository.findAll().stream()
                .map(provincia -> {
                    ProvinciaDTO dto = new ProvinciaDTO();
                    dto.setId(provincia.getId());
                    dto.setNombre(provincia.getNombre());
                    dto.setActivo(provincia.getActivo());
                    return dto;
                })
                .toList();
    }

    @Override
    public ProvinciaDTO obtenerProvinciaPorId(Integer id) {
        Provincias provincia = provinciaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Provincia no encontrada con ID: " + id));

        ProvinciaDTO dto = new ProvinciaDTO();
        dto.setId(provincia.getId());
        dto.setNombre(provincia.getNombre());
        dto.setActivo(provincia.getActivo());
        return dto;
    }
}
