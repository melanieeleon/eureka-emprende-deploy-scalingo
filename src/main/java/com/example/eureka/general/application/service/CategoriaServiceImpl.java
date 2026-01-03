package com.example.eureka.general.application.service;

import com.example.eureka.shared.storage.FileStorageService;
import com.example.eureka.general.infrastructure.dto.CategoriaRequestDTO;
import com.example.eureka.general.infrastructure.dto.CategoriasDTO;
import com.example.eureka.general.infrastructure.dto.converter.CategoriaDtoConverter;
import com.example.eureka.general.port.out.ICategoriasRepository;
import com.example.eureka.general.port.out.IMultimediaRepository;
import com.example.eureka.general.port.in.CategoriaService;
import com.example.eureka.general.domain.model.Categorias;
import com.example.eureka.entrepreneurship.domain.model.Multimedia;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaServiceImpl implements CategoriaService {

    private final ICategoriasRepository categoriasRepository;
    private final IMultimediaRepository multimediaRepository;
    private final CategoriaDtoConverter categoriaDtoConverter;
    private final FileStorageService fileStorageService;

    @Override
    public List<CategoriasDTO> listarCategoaria() {
        List<Categorias> categoriasList = categoriasRepository.findAll();
        return categoriasList.stream()
                .map(categoriaDtoConverter::convertirCategoriaModelToDto)
                .toList();
    }

    @Override
    public CategoriasDTO obtenerCategoriaPorId(Integer id) {
        Categorias categoria = categoriasRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No se encontró la categoría con id: " + id
                ));

        return categoriaDtoConverter.convertirCategoriaModelToDto(categoria);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CategoriasDTO crearCategoria(CategoriaRequestDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("El DTO no puede ser nulo");
        }

        // Crear y guardar multimedia si viene archivo
        Multimedia multimedia = null;
        if (dto.getArchivo() != null && !dto.getArchivo().isEmpty()) {
            try {
                // Subir archivo a S3 y obtener URL permanente
                String urlArchivo = fileStorageService.uploadFile(dto.getArchivo());

                // Crear entidad Multimedia
                multimedia = new Multimedia();
                multimedia.setUrlArchivo(urlArchivo);

                // Guardar multimedia en BD
                multimedia = multimediaRepository.save(multimedia);

            } catch (IOException e) {
                throw new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Error al subir el archivo a S3: " + e.getMessage()
                );
            }
        }

        // Crear categoría
        Categorias model = new Categorias();
        model.setNombre(dto.getNombre());
        model.setDescripcion(dto.getDescripcion());
        model.setMultimedia(multimedia);

        Categorias saved = categoriasRepository.save(model);

        return categoriaDtoConverter.convertirCategoriaModelToDto(saved);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CategoriasDTO actualizarCategoaria(Integer id, CategoriaRequestDTO dto) {
        // Buscar categoría existente
        Categorias categoria = categoriasRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Categoría no encontrada con ID: " + id
                ));

        // Actualizar datos básicos
        categoria.setNombre(dto.getNombre());
        categoria.setDescripcion(dto.getDescripcion());

        // Si viene un nuevo archivo, actualizar multimedia
        if (dto.getArchivo() != null && !dto.getArchivo().isEmpty()) {
            try {
                // Si ya tenía multimedia, eliminar el archivo viejo de S3
                if (categoria.getMultimedia() != null) {
                    String oldUrl = categoria.getMultimedia().getUrlArchivo();
                    // Extraer el nombre del archivo de la URL
                    String oldFileName = extractFileNameFromUrl(oldUrl);
                    if (oldFileName != null) {
                        fileStorageService.deleteFile(oldFileName);
                    }
                    // Eliminar registro de multimedia viejo
                    multimediaRepository.delete(categoria.getMultimedia());
                }

                // Subir nuevo archivo a S3
                String urlArchivo = fileStorageService.uploadFile(dto.getArchivo());

                // Crear nueva multimedia
                Multimedia multimedia = new Multimedia();
                multimedia.setUrlArchivo(urlArchivo);
                multimedia = multimediaRepository.save(multimedia);

                categoria.setMultimedia(multimedia);

            } catch (IOException e) {
                throw new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Error al actualizar el archivo en S3: " + e.getMessage()
                );
            }
        }

        Categorias updated = categoriasRepository.save(categoria);
        return categoriaDtoConverter.convertirCategoriaModelToDto(updated);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void eliminarCategoria(Integer id) {
        Categorias categoria = categoriasRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Categoría no encontrada con ID: " + id
                ));

        // Si tiene multimedia, eliminar archivo de S3
        if (categoria.getMultimedia() != null) {
            String url = categoria.getMultimedia().getUrlArchivo();
            String fileName = extractFileNameFromUrl(url);

            if (fileName != null) {
                try {
                    fileStorageService.deleteFile(fileName);
                } catch (Exception e) {
                    // Log error pero no fallar la eliminación
                    System.err.println("Error al eliminar archivo de S3: " + e.getMessage());
                }
            }
            // Eliminar categoría
            categoriasRepository.deleteById(id);

            // Eliminar registro de multimedia
            multimediaRepository.delete(categoria.getMultimedia());
        }
    }

    /**
     * Extraer nombre del archivo de la URL de S3
     * Ejemplo: https://bucket.s3.region.amazonaws.com/abc-123.jpg -> abc-123.jpg
     */
    private String extractFileNameFromUrl(String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }

        try {
            // Obtener la última parte de la URL después del último '/'
            int lastSlashIndex = url.lastIndexOf('/');
            if (lastSlashIndex != -1 && lastSlashIndex < url.length() - 1) {
                String fileName = url.substring(lastSlashIndex + 1);
                // Remover parámetros query si existen (por si hay URLs prefirmadas)
                int queryIndex = fileName.indexOf('?');
                if (queryIndex != -1) {
                    fileName = fileName.substring(0, queryIndex);
                }
                return fileName;
            }
        } catch (Exception e) {
            System.err.println("Error al extraer nombre de archivo de URL: " + e.getMessage());
        }

        return null;
    }


}