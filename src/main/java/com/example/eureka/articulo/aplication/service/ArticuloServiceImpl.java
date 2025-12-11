package com.example.eureka.articulo.aplication.service;

import com.example.eureka.articulo.infrastructure.dto.request.ArticuloRequestDTO;
import com.example.eureka.articulo.infrastructure.dto.response.ArticuloAdminDTO;
import com.example.eureka.articulo.infrastructure.dto.response.ArticuloPublicoDTO;
import com.example.eureka.articulo.infrastructure.dto.response.ArticuloResponseDTO;
import com.example.eureka.articulo.infrastructure.dto.response.TagDTO;
import com.example.eureka.articulo.port.in.ArticuloService;
import com.example.eureka.articulo.port.out.IArticuloRepository;
import com.example.eureka.articulo.port.out.ITagRepository;
import com.example.eureka.shared.exception.BusinessException;
import com.example.eureka.shared.storage.FileStorageService;
import com.example.eureka.articulo.infrastructure.specification.ArticuloSpecification;
import com.example.eureka.shared.enums.EstadoArticulo;
import com.example.eureka.articulo.domain.model.ArticulosBlog;
import com.example.eureka.entrepreneurship.domain.model.Multimedia;
import com.example.eureka.articulo.domain.model.TagsBlog;
import com.example.eureka.auth.domain.Usuarios;
import com.example.eureka.general.port.out.IMultimediaRepository;
import com.example.eureka.auth.port.out.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticuloServiceImpl implements ArticuloService {

    private final IArticuloRepository articuloRepository;
    private final ITagRepository tagRepository;
    private final IMultimediaRepository multimediaRepository;
    private final IUserRepository userRepository;
    private final FileStorageService fileStorageService;


    @Transactional
    public ArticuloResponseDTO crearArticulo(ArticuloRequestDTO dto, Integer idUsuario) {
        if (dto == null) {
            throw new BusinessException("El DTO no puede ser nulo");
        }

        // Validar usuario administrador
        validarAdministrador(idUsuario);

        Usuarios usuario = userRepository.findById(idUsuario)
                .orElseThrow(() -> new BusinessException("Usuario no encontrado"));

        // Subir imagen si fue enviada
        Multimedia multimedia = null;
        if (dto.getImagen() != null && !dto.getImagen().isEmpty()) {
            try {
                String urlArchivo = fileStorageService.uploadFile(dto.getImagen());
                multimedia = new Multimedia();
                multimedia.setUrlArchivo(urlArchivo);
                multimedia.setNombreActivo(dto.getImagen().getOriginalFilename());
                multimedia.setDescripcion("Imagen de artículo");
                multimedia = multimediaRepository.save(multimedia);
            } catch (IOException e) {
                throw new BusinessException("Error al subir el archivo: " + e.getMessage());
            }
        }

        // Crear artículo
        ArticulosBlog articulo = new ArticulosBlog();
        articulo.setTitulo(dto.getTitulo());
        articulo.setDescripcionCorta(dto.getDescripcionCorta());
        articulo.setContenido(dto.getContenido());
        articulo.setEstado(dto.getEstado() != null ? dto.getEstado() : EstadoArticulo.BORRADOR);
        articulo.setFechaCreacion(LocalDateTime.now());
        articulo.setImagen(multimedia);
        articulo.setUsuario(usuario);

        procesarTags(articulo, dto.getIdsTags(), dto.getNombresTags());

        ArticulosBlog articuloGuardado = articuloRepository.save(articulo);
        return convertirAResponseDTO(articuloGuardado);
    }



    @Override
    @Transactional(rollbackFor = Exception.class)
    public ArticuloResponseDTO editarArticulo(Integer idArticulo, ArticuloRequestDTO dto, Integer idUsuario) {
        validarAdministrador(idUsuario);

        ArticulosBlog articulo = articuloRepository.findById(idArticulo)
                .orElseThrow(() -> new BusinessException("Artículo no encontrado"));

        if (articulo.getEstado() == EstadoArticulo.ARCHIVADO) {
            throw new BusinessException("No se puede editar un artículo archivado");
        }

        articulo.setTitulo(dto.getTitulo());
        articulo.setDescripcionCorta(dto.getDescripcionCorta());
        articulo.setContenido(dto.getContenido());
        articulo.setEstado(dto.getEstado());
        articulo.setFechaModificacion(LocalDateTime.now());

        // Si viene una nueva imagen
        if (dto.getImagen() != null && !dto.getImagen().isEmpty()) {
            try {
                // Eliminar la imagen vieja si existe
                if (articulo.getImagen() != null) {
                    String oldUrl = articulo.getImagen().getUrlArchivo();
                    String oldFileName = extractFileNameFromUrl(oldUrl);
                    if (oldFileName != null) {
                        fileStorageService.deleteFile(oldFileName);
                    }
                    multimediaRepository.delete(articulo.getImagen());
                }

                // Subir nueva imagen
                String urlArchivo = fileStorageService.uploadFile(dto.getImagen());
                Multimedia multimedia = new Multimedia();
                multimedia.setUrlArchivo(urlArchivo);
                multimedia.setNombreActivo(dto.getImagen().getOriginalFilename());
                multimedia.setDescripcion("Imagen de artículo");
                multimedia = multimediaRepository.save(multimedia);

                articulo.setImagen(multimedia);

            } catch (IOException e) {
                throw new BusinessException("Error al actualizar la imagen: " + e.getMessage());
            }
        }

        articulo.getTags().clear();
        procesarTags(articulo, dto.getIdsTags(), dto.getNombresTags());

        ArticulosBlog articuloActualizado = articuloRepository.save(articulo);
        return convertirAResponseDTO(articuloActualizado);
    }


    @Transactional(readOnly = true)
    @Override
    public Page<ArticuloPublicoDTO> obtenerArticulosPublicos(
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            Integer idTag,
            String titulo,
            Pageable pageable) {

        Specification<ArticulosBlog> spec = ArticuloSpecification.conFiltros(
                EstadoArticulo.PUBLICADO,
                idTag,
                titulo,
                fechaInicio,
                fechaFin
        );

        Page<ArticulosBlog> articulos = articuloRepository.findAll(spec, pageable);
        return articulos.map(this::convertirAPublicoDTO);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<ArticuloAdminDTO> obtenerArticulosAdmin(
            EstadoArticulo estado,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            Integer idTag,
            String titulo,
            Pageable pageable) {

        Specification<ArticulosBlog> spec = ArticuloSpecification.conFiltros(
                estado,  // Puede ser null para ver todos los estados
                idTag,
                titulo,
                fechaInicio,
                fechaFin
        );

        Page<ArticulosBlog> articulos = articuloRepository.findAll(spec, pageable);
        return articulos.map(this::convertirAAdminDTO);
    }
    @Transactional
    @Override
    public void archivarArticulo(Integer idArticulo, Integer idUsuario) {
        validarAdministrador(idUsuario);

        ArticulosBlog articulo = articuloRepository.findById(idArticulo)
                .orElseThrow(() -> new BusinessException("Artículo no encontrado"));

        articulo.setEstado(EstadoArticulo.ARCHIVADO);
        articulo.setFechaModificacion(LocalDateTime.now());
        articuloRepository.save(articulo);
    }

    @Transactional
    @Override
    public void desarchivarArticulo(Integer idArticulo, Integer idUsuario) {
        validarAdministrador(idUsuario);

        ArticulosBlog articulo = articuloRepository.findById(idArticulo)
                .orElseThrow(() -> new BusinessException("Artículo no encontrado"));

        if (articulo.getEstado() != EstadoArticulo.ARCHIVADO) {
            throw new BusinessException("El artículo no está archivado");
        }

        articulo.setEstado(EstadoArticulo.PUBLICADO);
        articulo.setFechaModificacion(LocalDateTime.now());
        articuloRepository.save(articulo);
    }

    @Transactional(readOnly = true)
    @Override
    public ArticuloResponseDTO obtenerArticuloPublicoPorId(Integer idArticulo) {
        ArticulosBlog articulo = articuloRepository.findById(idArticulo)
                .orElseThrow(() -> new BusinessException("Artículo no encontrado"));

        if (articulo.getEstado() != EstadoArticulo.PUBLICADO) {
            throw new BusinessException("Artículo no disponible");
        }

        return convertirAResponseDTO(articulo);
    }

    @Transactional(readOnly = true)
    @Override
    public ArticuloResponseDTO obtenerArticuloPorId(Integer idArticulo) {
        ArticulosBlog articulo = articuloRepository.findById(idArticulo)
                .orElseThrow(() -> new BusinessException("Artículo no encontrado"));
        return convertirAResponseDTO(articulo);
    }

    @Transactional(readOnly = true)
    @Override
    public List<TagDTO> obtenerTodosTags() {
        List<TagsBlog> tags = tagRepository.findAll();
        return tags.stream()
                .map(tag -> TagDTO.builder()
                        .idTag(tag.getIdTag())
                        .nombre(tag.getNombre())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public TagDTO crearTag(String nombre, Integer idUsuario) {
        validarAdministrador(idUsuario);

        if (tagRepository.findByNombre(nombre).isPresent()) {
            throw new BusinessException("El tag ya existe");
        }

        TagsBlog tag = new TagsBlog();
        tag.setNombre(nombre);
        TagsBlog tagGuardado = tagRepository.save(tag);

        return TagDTO.builder()
                .idTag(tagGuardado.getIdTag())
                .nombre(tagGuardado.getNombre())
                .build();
    }

    // Métodos privados auxiliares

    private void procesarTags(ArticulosBlog articulo, List<Integer> idsTags, List<String> nombresTags) {
        if (idsTags != null && !idsTags.isEmpty()) {
            for (Integer idTag : idsTags) {
                TagsBlog tag = tagRepository.findById(idTag)
                        .orElseThrow(() -> new BusinessException("Tag no encontrado: " + idTag));
                articulo.getTags().add(tag);
            }
        }

        if (nombresTags != null && !nombresTags.isEmpty()) {
            List<String> nombresDistinct = nombresTags.stream()
                    .filter(nombre -> nombre != null && !nombre.trim().isEmpty())
                    .map(nombre -> nombre.trim().toLowerCase())
                    .distinct()
                    .collect(Collectors.toList());

            List<TagsBlog> existentes = tagRepository.findAllByNombreInIgnoreCase(nombresDistinct);


            var mapaExistentes = existentes.stream()
                    .collect(Collectors.toMap(tag -> tag.getNombre(), tag -> tag));

            for (String nombreTag : nombresDistinct) {
                TagsBlog tag = mapaExistentes.get(nombreTag);
                if (tag == null) {
                    TagsBlog nuevoTag = new TagsBlog();
                    nuevoTag.setNombre(nombreTag);  // Siempre en minúsculas
                    tag = tagRepository.save(nuevoTag);
                    mapaExistentes.put(nombreTag, tag);
                }
                articulo.getTags().add(tag);
            }
        }
    }

    private void validarAdministrador(Integer idUsuario) {
        if (idUsuario == null) {
            throw new BusinessException("Usuario no especificado");
        }

        Usuarios usuario = userRepository.findById(idUsuario.intValue())
                .orElseThrow(() -> new BusinessException("Usuario no encontrado"));

        if (usuario.getRol() == null || !usuario.getRol().getNombre().equalsIgnoreCase("Administrador")) {
            throw new BusinessException("Solo los administradores pueden realizar esta acción");
        }
    }

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


    private List<TagDTO> convertirTags(List<TagsBlog> tags) {
        return tags.stream()
                .map(tag -> TagDTO.builder()
                        .idTag(tag.getIdTag())
                        .nombre(tag.getNombre())
                        .build())
                .collect(Collectors.toList());
    }
    private ArticuloPublicoDTO convertirAPublicoDTO(ArticulosBlog articulo) {
        return ArticuloPublicoDTO.builder()
                .idArticulo(articulo.getIdArticulo())
                .titulo(articulo.getTitulo())
                .descripcionCorta(articulo.getDescripcionCorta())
                .idImagen(articulo.getImagen().getId())
                .urlImagen(articulo.getImagen().getUrlArchivo())
                .fechaCreacion(articulo.getFechaCreacion())
                .tags(convertirTags(new ArrayList<>(articulo.getTags())))  // ← Convertir Set a List
                .build();
    }

    private ArticuloAdminDTO convertirAAdminDTO(ArticulosBlog articulo) {
        return ArticuloAdminDTO.builder()
                .idArticulo(articulo.getIdArticulo())
                .titulo(articulo.getTitulo())
                .descripcionCorta(articulo.getDescripcionCorta())
                .idImagen(articulo.getImagen().getId())
                .urlImagen(articulo.getImagen().getUrlArchivo())
                .fechaCreacion(articulo.getFechaCreacion())
                .estado(articulo.getEstado())
                .fechaModificacion(articulo.getFechaModificacion())
                .idUsuario(articulo.getUsuario().getId())
                .nombreUsuario(articulo.getUsuario().getNombre() + " " + articulo.getUsuario().getApellido())
                .tags(convertirTags(new ArrayList<>(articulo.getTags())))  // ← Convertir Set a List
                .build();
    }

    private ArticuloResponseDTO convertirAResponseDTO(ArticulosBlog articulo) {
        return ArticuloResponseDTO.builder()
                .idArticulo(articulo.getIdArticulo())
                .titulo(articulo.getTitulo())
                .descripcionCorta(articulo.getDescripcionCorta())
                .contenido(articulo.getContenido())
                .estado(articulo.getEstado())
                .fechaCreacion(articulo.getFechaCreacion())
                .fechaModificacion(articulo.getFechaModificacion())
                .idImagen(articulo.getImagen().getId())
                .urlImagen(articulo.getImagen().getUrlArchivo())
                .idUsuario(articulo.getUsuario().getId())
                .nombreUsuario(articulo.getUsuario().getNombre() + " " + articulo.getUsuario().getApellido())
                .tags(convertirTags(new ArrayList<>(articulo.getTags())))  // ← Convertir Set a List
                .build();
    }
}