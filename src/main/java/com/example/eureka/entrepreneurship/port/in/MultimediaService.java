package com.example.eureka.entrepreneurship.port.in;

import com.example.eureka.entrepreneurship.infrastructure.dto.shared.MultimediaDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MultimediaService {

    /**
     * Agregar una imagen al emprendimiento
     */
    void agregarImagen(Integer emprendimientoId, MultipartFile archivo, String tipo);

    /**
     * Agregar múltiples imágenes al emprendimiento
     */
    void agregarImagenes(Integer emprendimientoId, List<MultipartFile> archivos, List<String> tipos);

    /**
     * Eliminar una imagen del emprendimiento
     */
    void eliminarImagen(Integer emprendimientoId, Long multimediaId);

    /**
     * Obtener toda la multimedia de un emprendimiento
     */
    List<MultimediaDTO> obtenerMultimedia(Integer emprendimientoId);

    /**
     * Obtener multimedia por tipo
     */
    List<MultimediaDTO> obtenerMultimediaPorTipo(Integer emprendimientoId, String tipo);
}