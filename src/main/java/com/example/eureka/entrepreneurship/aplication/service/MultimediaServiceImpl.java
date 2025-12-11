package com.example.eureka.entrepreneurship.aplication.service;

import com.example.eureka.shared.storage.FileStorageService;
import com.example.eureka.entrepreneurship.infrastructure.dto.shared.MultimediaDTO;
import com.example.eureka.entrepreneurship.infrastructure.mappers.EmprendimientoMapper;
import com.example.eureka.entrepreneurship.port.out.IEmprendimientoMultimediaRepository;
import com.example.eureka.entrepreneurship.port.out.IEmprendimientosRepository;
import com.example.eureka.entrepreneurship.port.in.MultimediaService;
import com.example.eureka.general.port.out.IMultimediaRepository;
import com.example.eureka.entrepreneurship.domain.model.EmprendimientoMultimedia;
import com.example.eureka.entrepreneurship.domain.model.Emprendimientos;
import com.example.eureka.entrepreneurship.domain.model.Multimedia;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MultimediaServiceImpl implements MultimediaService {

    private final IEmprendimientosRepository emprendimientosRepository;
    private final IMultimediaRepository multimediaRepository;
    private final IEmprendimientoMultimediaRepository emprendimientoMultimediaRepository;
    private final FileStorageService fileStorageService;

    @Override
    @Transactional
    public void agregarImagen(Integer emprendimientoId, MultipartFile archivo, String tipo) {
        log.info("Agregando imagen al emprendimiento {}, tipo: {}", emprendimientoId, tipo);

        // Validar emprendimiento
        Emprendimientos emprendimiento = emprendimientosRepository.findById(emprendimientoId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Emprendimiento no encontrado con ID: " + emprendimientoId));

        try {
            // 1. Subir archivo a S3
            String urlArchivo = fileStorageService.uploadFile(archivo);

            // 2. Crear entidad Multimedia
            Multimedia multimedia = new Multimedia();
            multimedia.setUrlArchivo(urlArchivo);
            multimedia.setNombreActivo(archivo.getOriginalFilename());
            multimedia = multimediaRepository.save(multimedia);

            // 3. Crear relación emprendimiento-multimedia
            EmprendimientoMultimedia empMultimedia = new EmprendimientoMultimedia();
            empMultimedia.setEmprendimiento(emprendimiento);
            empMultimedia.setMultimedia(multimedia);
            empMultimedia.setTipo(tipo);
            emprendimientoMultimediaRepository.save(empMultimedia);

            log.info("Imagen agregada exitosamente: {}", urlArchivo);

        } catch (IOException e) {
            log.error("Error al subir archivo: {}", archivo.getOriginalFilename(), e);
            throw new RuntimeException("Error al subir archivo: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void agregarImagenes(Integer emprendimientoId, List<MultipartFile> archivos, List<String> tipos) {
        if (CollectionUtils.isEmpty(archivos)) {
            log.warn("No se proporcionaron archivos para agregar");
            return;
        }

        log.info("Agregando {} imágenes al emprendimiento {}", archivos.size(), emprendimientoId);

        for (int i = 0; i < archivos.size(); i++) {
            String tipo = (tipos != null && i < tipos.size()) ? tipos.get(i) : "GALERIA";
            agregarImagen(emprendimientoId, archivos.get(i), tipo);
        }
    }

    @Override
    @Transactional
    public void eliminarImagen(Integer emprendimientoId, Long multimediaId) {
        log.info("Eliminando imagen {} del emprendimiento {}", multimediaId, emprendimientoId);

        // Buscar la relación emprendimiento-multimedia
        EmprendimientoMultimedia empMultimedia = emprendimientoMultimediaRepository
                .findByEmprendimientoId(emprendimientoId).stream()
                .filter(em -> em.getMultimedia().getId().equals(multimediaId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(
                        "Multimedia no encontrada con ID: " + multimediaId +
                                " para el emprendimiento: " + emprendimientoId));

        Multimedia multimedia = empMultimedia.getMultimedia();

        try {
            // 1. Eliminar archivo de S3
            String fileName = extractFileNameFromUrl(multimedia.getUrlArchivo());
            if (fileName != null) {
                fileStorageService.deleteFile(fileName);
                log.info("Archivo eliminado de S3: {}", fileName);
            }

            // 2. Eliminar relación emprendimiento-multimedia
            emprendimientoMultimediaRepository.delete(empMultimedia);

            // 3. Eliminar registro de multimedia
            multimediaRepository.delete(multimedia);

            log.info("Imagen eliminada exitosamente");

        } catch (Exception e) {
            log.error("Error al eliminar imagen: {}", e.getMessage(), e);
            throw new RuntimeException("Error al eliminar imagen: " + e.getMessage());
        }
    }

    @Override
    public List<MultimediaDTO> obtenerMultimedia(Integer emprendimientoId) {
        log.info("Obteniendo multimedia del emprendimiento {}", emprendimientoId);

        // Verificar que existe el emprendimiento
        if (!emprendimientosRepository.existsById(emprendimientoId)) {
            throw new EntityNotFoundException(
                    "Emprendimiento no encontrado con ID: " + emprendimientoId);
        }

        List<EmprendimientoMultimedia> multimedia =
                emprendimientoMultimediaRepository.findByEmprendimientoId(emprendimientoId);

        return EmprendimientoMapper.toMultimediaDTOList(multimedia);
    }

    @Override
    public List<MultimediaDTO> obtenerMultimediaPorTipo(Integer emprendimientoId, String tipo) {
        log.info("Obteniendo multimedia tipo {} del emprendimiento {}", tipo, emprendimientoId);

        // Verificar que existe el emprendimiento
        if (!emprendimientosRepository.existsById(emprendimientoId)) {
            throw new EntityNotFoundException(
                    "Emprendimiento no encontrado con ID: " + emprendimientoId);
        }

        List<EmprendimientoMultimedia> multimedia =
                emprendimientoMultimediaRepository.findByEmprendimientoIdAndTipo(emprendimientoId, tipo);

        return EmprendimientoMapper.toMultimediaDTOList(multimedia);
    }

    /**
     * Extraer nombre del archivo de la URL de S3
     */
    private String extractFileNameFromUrl(String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }

        try {
            int lastSlashIndex = url.lastIndexOf('/');
            if (lastSlashIndex != -1 && lastSlashIndex < url.length() - 1) {
                String fileName = url.substring(lastSlashIndex + 1);
                int queryIndex = fileName.indexOf('?');
                if (queryIndex != -1) {
                    fileName = fileName.substring(0, queryIndex);
                }
                return fileName;
            }
        } catch (Exception e) {
            log.error("Error al extraer nombre de archivo de URL: {}", e.getMessage());
        }

        return null;
    }
}