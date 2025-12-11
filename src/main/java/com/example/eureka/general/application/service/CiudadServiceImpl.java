package com.example.eureka.general.application.service;

import com.example.eureka.general.infrastructure.dto.CiudadDTO;
import com.example.eureka.general.infrastructure.dto.ProvinciaDTO;
import com.example.eureka.general.port.out.ICiudadesRepository;
import com.example.eureka.general.port.out.IProvinciaRepository;
import com.example.eureka.general.port.in.CiudadService;
import com.example.eureka.general.domain.model.Ciudades;
import com.example.eureka.general.domain.model.Provincias;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CiudadServiceImpl implements CiudadService {

    private final ICiudadesRepository ciudadRepository;
    private final IProvinciaRepository provinciaRepository;

    private CiudadDTO mapToDTO(Ciudades ciudad) {
        Provincias provincia = ciudad.getProvincias();
        ProvinciaDTO provinciaDTO = new ProvinciaDTO();
        provinciaDTO.setId(provincia.getId());
        provinciaDTO.setNombre(provincia.getNombre());
        provinciaDTO.setActivo(provincia.getActivo());

        CiudadDTO dto = new CiudadDTO();
        dto.setId(ciudad.getId());
        dto.setNombreCiudad(ciudad.getNombreCiudad());
        dto.setProvincia(provinciaDTO);
        return dto;
    }

    @Override
    public List<CiudadDTO> listar() {
        return ciudadRepository.findAll()
                .stream().map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CiudadDTO obtenerPorId(Integer id) {
        Ciudades ciudad = ciudadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ciudad no encontrada"));
        return mapToDTO(ciudad);
    }

    public List<CiudadDTO> obtenerCiudadPorProvinciaId(Integer id) {
        return ciudadRepository.findByProvincias_Id(id).stream().map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CiudadDTO guardar(CiudadDTO dto) {
        Provincias provincia = provinciaRepository.findById(dto.getProvincia().getId())
                .orElseThrow(() -> new RuntimeException("Provincia no encontrada"));

        Ciudades ciudad = new Ciudades();
        ciudad.setNombreCiudad(dto.getNombreCiudad());
        ciudad.setProvincias(provincia);

        Ciudades guardada = ciudadRepository.save(ciudad);
        return mapToDTO(guardada);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CiudadDTO actualizar(Integer id, CiudadDTO dto) {
        Ciudades ciudad = ciudadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ciudad no encontrada"));

        Provincias provincia = provinciaRepository.findById(dto.getProvincia().getId())
                .orElseThrow(() -> new RuntimeException("Provincia no encontrada"));

        ciudad.setNombreCiudad(dto.getNombreCiudad());
        ciudad.setProvincias(provincia);

        Ciudades actualizada = ciudadRepository.save(ciudad);
        return mapToDTO(actualizada);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void eliminar(Integer id) {
        if (!ciudadRepository.existsById(id)) {
            throw new RuntimeException("Ciudad no encontrada");
        }
        ciudadRepository.deleteById(id);
    }
}
