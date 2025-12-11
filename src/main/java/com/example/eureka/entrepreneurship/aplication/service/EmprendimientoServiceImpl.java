package com.example.eureka.entrepreneurship.aplication.service;

import com.example.eureka.auth.domain.Usuarios;
import com.example.eureka.auth.port.out.IUserRepository;
import com.example.eureka.entrepreneurship.domain.model.*;
import com.example.eureka.entrepreneurship.infrastructure.dto.publico.EmprendimientoListaPublicoDTO;
import com.example.eureka.entrepreneurship.infrastructure.dto.publico.MiniEmprendimientoDTO;
import com.example.eureka.entrepreneurship.infrastructure.dto.shared.*;
import com.example.eureka.entrepreneurship.port.out.*;
import com.example.eureka.general.domain.model.*;
import com.example.eureka.general.port.out.*;
import com.example.eureka.entrepreneurship.infrastructure.dto.request.EmprendimientoRequestDTO;
import com.example.eureka.metricas.domain.MetricasBasicas;
import com.example.eureka.shared.storage.FileStorageService;
import com.example.eureka.entrepreneurship.infrastructure.mappers.EmprendimientoMapper;
import com.example.eureka.entrepreneurship.port.in.EmprendimientoService;
import com.example.eureka.shared.enums.EstadoEmprendimiento;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmprendimientoServiceImpl implements EmprendimientoService {

    private final IUserRepository userRepository;
    private final ICiudadesRepository ciudadesRepository;
    private final IEmprendimientosRepository emprendimientosRepository;
    private final IEmprendimientoCategoriasRepository emprendimientoCategoriasRepository;
    private final IEmprendimientosDescripcionRepository emprendimientosDescripcionRepository;
    private final IEmprendimientoPresenciaDigitalRepository emprendimientoPresenciaDigitalRepository;
    private final IEmprendimientoMetricaRepository emprendimientoMetricaRepository;
    private final IEmprendimientoDeclaracionesRepository emprendimientoDeclaracionesRepository;
    private final IEmprendimientoParticicipacionComunidadRepository emprendimientoParticicipacionComunidadRepository;
    private final ITiposMetricasRepository tiposMetricasRepository;
    private final IRepresentanteInformacionRepository representanteInformacionRepository;
    private final IOpcionesParticipacionComunidadRepository opcionesParticipacionComunidadRepository;
    private final IDeclaracionesFinalesRepository declaracionesFinalesRepository;
    private final ICategoriaRepository categoriaRepository;
    private final ITiposEmprendimientoRepository tiposEmprendimientoRepository;
    private final IRepresentanteInformacionRepository informacionRepresentanteRepository;
    private final SolicitudAprobacionService solicitudAprobacionService;

    // NUEVAS DEPENDENCIAS PARA MULTIMEDIA
    private final IMultimediaRepository multimediaRepository;
    private final IEmprendimientoMultimediaRepository emprendimientoMultimediaRepository;
    private final FileStorageService fileStorageService;
    private final ICategoriaRepository categoriasRepository;


    @Override
    @Transactional
    public SolicitudAprobacion enviarParaAprobacion(Integer emprendimientoId, Usuarios usuario) {
        log.info("Enviando emprendimiento {} para aprobación", emprendimientoId);
        EmprendimientoCompletoDTO datosCompletos = solicitudAprobacionService
                .capturarEstadoCompleto(emprendimientoId);
        return solicitudAprobacionService.crearSolicitud(emprendimientoId, datosCompletos, usuario);
    }

    @Override
    public VistaEmprendedorDTO obtenerVistaEmprendedor(Integer emprendimientoId) {
        return solicitudAprobacionService.obtenerVistaEmprendedor(emprendimientoId);
    }

    @Override
    @Transactional
    public Integer estructuraEmprendimiento(@Valid EmprendimientoRequestDTO emprendimientoRequestDTO) {
        log.info("Iniciando creación de estructura de emprendimiento para usuario: {}",
                emprendimientoRequestDTO.getUsuarioId());

        // Validaciones
        if (emprendimientoRequestDTO == null) {
            throw new IllegalArgumentException("Request no puede ser nulo");
        }
        if (emprendimientoRequestDTO.getUsuarioId() == null) {
            throw new IllegalArgumentException("Usuario ID no puede ser nulo");
        }
        if (emprendimientoRequestDTO.getEmprendimiento() == null) {
            throw new IllegalArgumentException("Datos del emprendimiento no pueden ser nulos");
        }

        // Buscar usuario
        Usuarios usuario = userRepository.findById(emprendimientoRequestDTO.getUsuarioId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Usuario no encontrado con ID: " + emprendimientoRequestDTO.getUsuarioId()));

        // Crear emprendimiento principal
        Emprendimientos emprendimiento = crearEmprendimiento(
                emprendimientoRequestDTO.getEmprendimiento(),
                usuario,
                emprendimientoRequestDTO.getTipoAccion()
        );

        // Procesar multimedia si existen archivos
        if (!CollectionUtils.isEmpty(emprendimientoRequestDTO.getImagenes())) {
            agregarMultimediaEmprendimiento(
                    emprendimiento,
                    emprendimientoRequestDTO.getImagenes(),
                    emprendimientoRequestDTO.getTiposMultimedia()
            );
        }

        // Agregar todas las relaciones solo si es CREAR (no borrador)
        if ("CREAR".equals(emprendimientoRequestDTO.getTipoAccion())) {
            agregarCategoriaEmprendimiento(emprendimiento, emprendimientoRequestDTO.getCategorias());
            agregarDescripcionEmprendimiento(emprendimiento, emprendimientoRequestDTO.getDescripciones());
            agregarMetricasEmprendimiento(emprendimiento, emprendimientoRequestDTO.getMetricas());
            agregarPresenciaDigitalEmprendimiento(emprendimiento, emprendimientoRequestDTO.getPresenciasDigitales());
            agregarParticipacionComunidad(emprendimiento, emprendimientoRequestDTO.getParticipacionesComunidad());
            agregarDeclaracionesFinales(emprendimiento, emprendimientoRequestDTO.getDeclaracionesFinales());
        }

        log.info("Emprendimiento creado exitosamente with ID: {}", emprendimiento.getId());
        return emprendimiento.getId();
    }

    /**
     * NUEVO: Método para agregar multimedia al emprendimiento
     */
    private void agregarMultimediaEmprendimiento(
            Emprendimientos emprendimiento,
            List<MultipartFile> archivos,
            List<String> tipos) {

        if (CollectionUtils.isEmpty(archivos)) {
            log.debug("No hay archivos multimedia para agregar");
            return;
        }

        log.debug("Agregando {} archivos multimedia al emprendimiento: {}",
                archivos.size(), emprendimiento.getId());

        for (int i = 0; i < archivos.size(); i++) {
            MultipartFile archivo = archivos.get(i);
            String tipo = (tipos != null && i < tipos.size()) ? tipos.get(i) : "GALERIA";

            try {
                // 1. Subir archivo a S3
                String urlArchivo = fileStorageService.uploadFile(archivo);

                // 2. Crear entidad Multimedia
                Multimedia multimedia = new Multimedia();
                multimedia.setUrlArchivo(urlArchivo);
                multimedia.setNombreActivo(archivo.getOriginalFilename());

                // Guardar multimedia
                multimedia = multimediaRepository.save(multimedia);

                // 3. Crear relación emprendimiento-multimedia
                EmprendimientoMultimedia empMultimedia = new EmprendimientoMultimedia();
                empMultimedia.setEmprendimiento(emprendimiento);
                empMultimedia.setMultimedia(multimedia);
                empMultimedia.setTipo(tipo); // "LOGO", "PORTADA", "GALERIA", etc.

                emprendimientoMultimediaRepository.save(empMultimedia);

                log.info("Archivo subido exitosamente: {} - Tipo: {}", urlArchivo, tipo);

            } catch (IOException e) {
                log.error("Error al subir archivo: {}", archivo.getOriginalFilename(), e);
                throw new RuntimeException("Error al subir archivo multimedia: " + e.getMessage());
            }
        }
    }

    private Emprendimientos crearEmprendimiento(EmprendimientoDTO emprendimientoDTO, Usuarios usuario, String tipoAccion) {
        log.debug("Creando emprendimiento con nombre: {}", emprendimientoDTO.getNombreComercialEmprendimiento());

        Ciudades ciudad = ciudadesRepository.findById(emprendimientoDTO.getCiudad())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Ciudad no encontrada con ID: " + emprendimientoDTO.getCiudad()));

        TiposEmprendimientos tipoEmprendimiento = tiposEmprendimientoRepository
                .findById(emprendimientoDTO.getTipoEmprendimientoId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Tipo de emprendimiento no encontrado con ID: " + emprendimientoDTO.getTipoEmprendimientoId()));

        Emprendimientos emprendimiento = new Emprendimientos();
        emprendimiento.setNombreComercial(emprendimientoDTO.getNombreComercialEmprendimiento());
        emprendimiento.setAnioCreacion(emprendimientoDTO.getFechaCreacion());
        emprendimiento.setActivoEmprendimiento(emprendimientoDTO.getEstadoEmpredimiento());
        emprendimiento.setAceptaDatosPublicos(emprendimientoDTO.getDatosPublicos());
        emprendimiento.setFechaCreacion(emprendimientoDTO.getFechaCreacion());

        if ("BORRADOR".equals(tipoAccion)) {
            emprendimiento.setEstadoEmprendimiento(String.valueOf(EstadoEmprendimiento.BORRADOR));
        } else {
            emprendimiento.setEstadoEmprendimiento(String.valueOf(EstadoEmprendimiento.PENDIENTE_APROBACION));
        }

        emprendimiento.setCiudades(ciudad);
        emprendimiento.setUsuarios(usuario);
        emprendimiento.setTiposEmprendimientos(tipoEmprendimiento);

        return emprendimientosRepository.save(emprendimiento);
    }

    // ... (resto de métodos sin cambios hasta actualizarEmprendimiento)

    @Override
    @Transactional
    public EmprendimientoResponseDTO actualizarEmprendimiento(Integer id, EmprendimientoRequestDTO emprendimientoRequestDTO) throws Exception {
        Emprendimientos emprendimiento = emprendimientosRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Emprendimiento no encontrado con ID: " + id));

        // Actualizar campos principales
        EmprendimientoDTO dto = emprendimientoRequestDTO.getEmprendimiento();
        if (dto != null) {
            emprendimiento.setNombreComercial(dto.getNombreComercialEmprendimiento());
            emprendimiento.setAnioCreacion(dto.getFechaCreacion());
            emprendimiento.setActivoEmprendimiento(dto.getEstadoEmpredimiento());
            emprendimiento.setAceptaDatosPublicos(dto.getDatosPublicos());
            emprendimiento.setFechaCreacion(dto.getFechaCreacion());

            if (dto.getCiudad() != null) {
                Ciudades ciudad = ciudadesRepository.findById(dto.getCiudad())
                        .orElseThrow(() -> new EntityNotFoundException("Ciudad no encontrada con ID: " + dto.getCiudad()));
                emprendimiento.setCiudades(ciudad);
            }
            if (dto.getTipoEmprendimientoId() != null) {
                TiposEmprendimientos tipoEmprendimiento = tiposEmprendimientoRepository.findById(dto.getTipoEmprendimientoId())
                        .orElseThrow(() -> new EntityNotFoundException("Tipo de emprendimiento no encontrado con ID: " + dto.getTipoEmprendimientoId()));
                emprendimiento.setTiposEmprendimientos(tipoEmprendimiento);
            }
        }
        emprendimientosRepository.save(emprendimiento);

        // NUEVO: Actualizar multimedia si vienen nuevas imágenes
        if (!CollectionUtils.isEmpty(emprendimientoRequestDTO.getImagenes())) {
            agregarMultimediaEmprendimiento(
                    emprendimiento,
                    emprendimientoRequestDTO.getImagenes(),
                    emprendimientoRequestDTO.getTiposMultimedia()
            );
        }

        // Actualizar relaciones
        emprendimientoCategoriasRepository.deleteEmprendimientoCategoriasByEmprendimientoId(id);
        agregarCategoriaEmprendimiento(emprendimiento, emprendimientoRequestDTO.getCategorias());

        // ... (resto del código de actualización sin cambios)
        actualizarDescripciones(id, emprendimiento, emprendimientoRequestDTO.getDescripciones());
        actualizarMetricas(id, emprendimiento, emprendimientoRequestDTO.getMetricas());
        actualizarPresenciaDigital(id, emprendimiento, emprendimientoRequestDTO.getPresenciasDigitales());
        actualizarParticipacionComunidad(id, emprendimiento, emprendimientoRequestDTO.getParticipacionesComunidad());
        actualizarDeclaraciones(id, emprendimiento, emprendimientoRequestDTO.getDeclaracionesFinales());

        return obtenerEmprendimientoCompletoPorId(id);
    }

    // Métodos auxiliares para actualización (extraídos para mejor organización)
    private void actualizarDescripciones(Integer id, Emprendimientos emprendimiento, List<EmprendimientoDescripcionDTO> nuevas) {
        List<TiposDescripcionEmprendimiento> actuales = emprendimientosDescripcionRepository.findByEmprendimientoId(id);
        List<EmprendimientoDescripcionDTO> nuevasDescripciones = nuevas != null ? nuevas : List.of();

        for (TiposDescripcionEmprendimiento actual : actuales) {
            boolean existe = nuevasDescripciones.stream()
                    .anyMatch(d -> d.getTipoDescripcion().equals(actual.getTipoDescripcion()));
            if (!existe) {
                emprendimientosDescripcionRepository.delete(actual);
            }
        }

        for (EmprendimientoDescripcionDTO nueva : nuevasDescripciones) {
            TiposDescripcionEmprendimiento actual = actuales.stream()
                    .filter(d -> d.getTipoDescripcion().equals(nueva.getTipoDescripcion()))
                    .findFirst()
                    .orElse(null);

            if (actual != null) {
                actual.setDescripcion(nueva.getDescripcion());
                actual.setMaxCaracteres(nueva.getMaxCaracteres());
                actual.setObligatorio(nueva.getObligatorio());
                emprendimientosDescripcionRepository.save(actual);
            } else {
                TiposDescripcionEmprendimiento nuevaDesc = new TiposDescripcionEmprendimiento();
                nuevaDesc.setTipoDescripcion(nueva.getTipoDescripcion());
                nuevaDesc.setDescripcion(nueva.getDescripcion());
                nuevaDesc.setMaxCaracteres(nueva.getMaxCaracteres());
                nuevaDesc.setObligatorio(nueva.getObligatorio());
                nuevaDesc.setEmprendimiento(emprendimiento);
                emprendimientosDescripcionRepository.save(nuevaDesc);
            }
        }
    }

    // ... (incluir los otros métodos de actualización similarmente)

    // ... (resto de métodos sin cambios)

    private void agregarCategoriaEmprendimiento(Emprendimientos emprendimientos, List<EmprendimientoCategoriaDTO> lsCategorias) {
        if (CollectionUtils.isEmpty(lsCategorias)) {
            log.debug("No hay categorías para agregar");
            return;
        }

        log.debug("Agregando {} categorías al emprendimiento: {}", lsCategorias.size(), emprendimientos);

        /*List<EmprendimientoCategorias> categorias = lsCategorias.stream()
                .map(categoriaDTO -> {
                    EmprendimientoCategorias categoria = new EmprendimientoCategorias();
                    categoria.setEmprendimiento(emprendimientos);
                    Categorias cat = emprendimientoCategoriasRepository.findById(categoriaDTO.getCategoria().getId())
                            .orElseThrow(() -> new EntityNotFoundException(
                                    "Categoría no encontrada con ID: " + categoriaDTO.getCategoria())).getCategoria();
                    categoria.setCategoria(cat);
                    return categoria;
                })
                .collect(Collectors.toList());*/


        List<EmprendimientoCategorias> categorias = lsCategorias.stream()
                .map(categoriaDTO -> {

                    // 1. Buscar categoría REAL
                    Categorias categoria = categoriasRepository.findById(categoriaDTO.getCategoria().getId())
                            .orElseThrow(() -> new EntityNotFoundException(
                                    "Categoría no encontrada con ID: " + categoriaDTO.getCategoria().getId()));

                    // 2. Crear ID compuesto
                    EmprendimientoCategoriasId id = new EmprendimientoCategoriasId(
                            emprendimientos.getId(),
                            categoria.getId()
                    );

                    // 3. Crear entidad
                    EmprendimientoCategorias ec = new EmprendimientoCategorias();
                    ec.setId(id);
                    ec.setEmprendimiento(emprendimientos);
                    ec.setCategoria(categoria);

                    return ec;
                })
                .collect(Collectors.toList());

        emprendimientoCategoriasRepository.saveAll(categorias);
    }

    // TODO: Ajustado para obtener solo aprobados
    @Override
    public List<MiniEmprendimientoDTO> obtenerEmprendimientos() {
        List<Emprendimientos> lista = emprendimientosRepository.findByEstadoEmprendimiento("APROBADO");

        if (lista.isEmpty()) {
            return Collections.emptyList();
        }

        // Obtener IDs de emprendimientos
        List<Integer> empIds = lista.stream()
                .map(Emprendimientos::getId)
                .collect(Collectors.toList());

        // Cargar categorías en batch
        Map<Integer, List<String>> categoriasMap = emprendimientoCategoriasRepository
                .findByEmprendimientoIdIn(empIds)
                .stream()
                .collect(Collectors.groupingBy(
                        ec -> ec.getEmprendimiento().getId(),
                        Collectors.mapping(ec -> ec.getCategoria().getNombre(), Collectors.toList())
                ));

        // Cargar descripciones en batch
        Map<Integer, String> descripcionesMap = emprendimientosDescripcionRepository
                .findByEmprendimientoIdIn(empIds)
                .stream()
                .filter(d -> "1".equals(d.getTipoDescripcion()))
                .collect(Collectors.toMap(
                        d -> d.getEmprendimiento().getId(),
                        TiposDescripcionEmprendimiento::getDescripcion,
                        (existing, replacement) -> existing // En caso de duplicados, mantener el primero
                ));

        // Mapear a DTOs
        return lista.stream()
                .map(emp -> MiniEmprendimientoDTO.builder()
                        .id(emp.getId())
                        .nombreComercial(emp.getNombreComercial())
                        .ciudad(emp.getCiudades() != null ? emp.getCiudades().getNombreCiudad() : null)
                        .categorias(categoriasMap.getOrDefault(emp.getId(), null))
                        .descripcion(descripcionesMap.getOrDefault(emp.getId(), null))
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<EmprendimientoListaPublicoDTO> obtenesListaDeEmprendimientos(Usuarios usuario) {

        // Validar que el usuario no sea null
        if (usuario == null) {
            throw new RuntimeException("Usuario no autenticado");
        }

        List<Emprendimientos> lista;
        System.out.println("Rol del usuario: " + usuario.getRol());
        System.out.println("Usuario: " + usuario);

        if (usuario.getRol().getNombre().equals("ADMINISTRADOR")) {
            lista = emprendimientosRepository.findAll();
        } else {
            lista = emprendimientosRepository.findByUsuariosAndEstadoEmprendimientoEquals(usuario,"APROBADO");
        }

        // Validar si la lista está vacía
        if (lista.isEmpty()) {
            throw new RuntimeException("No se encontraron emprendimientos");
        }

        return lista.stream()
                .map(emp -> EmprendimientoListaPublicoDTO.builder()
                        .nombreEmprendimiento(emp.getNombreComercial())
                        .build())
                .toList();
    }


    @Override
    public EmprendimientoResponseDTO obtenerEmprendimientoPorId(Integer id) {
        Emprendimientos emp = emprendimientosRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Emprendimiento no encontrado con id: " + id));
        return EmprendimientoMapper.toResponseDTO(emp);
    }

    @Override
    @Transactional
    public EmprendimientoResponseDTO obtenerEmprendimientoCompletoPorId(Integer id) {
        Emprendimientos emprendimiento = emprendimientosRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Emprendimiento no encontrado con id: " + id));

        EmprendimientoResponseDTO dto = EmprendimientoMapper.toResponseDTO(emprendimiento);
        dto.setCategorias(EmprendimientoMapper.toCategoriaDTOList(
                emprendimientoCategoriasRepository.findEmprendimientosPorCategoria(id)
        ));
        dto.setDescripciones(EmprendimientoMapper.toDescripcionDTOList(
                emprendimientosDescripcionRepository.findByEmprendimientoId(id)
        ));
        dto.setPresenciasDigitales(EmprendimientoMapper.toPresenciaDigitalDTOList(
                emprendimientoPresenciaDigitalRepository.findByEmprendimientoId(id)
        ));
        dto.setMetricas(EmprendimientoMapper.toMetricasDTOList(
                emprendimientoMetricaRepository.findByEmprendimientoId(id)
        ));
        dto.setDeclaracionesFinales(EmprendimientoMapper.toDeclaracionesDTOList(
                emprendimientoDeclaracionesRepository.findByEmprendimientoId(id)
        ));
        dto.setParticipacionesComunidad(EmprendimientoMapper.toParticipacionDTOList(
                emprendimientoParticicipacionComunidadRepository.findByEmprendimientoIdFetchOpcion(id)
        ));
        dto.setInformacionRepresentante(EmprendimientoMapper.toRepresentanteDTO(
                informacionRepresentanteRepository.findFirstByEmprendimientoId(id)
        ));

        // NUEVO: Incluir multimedia
        dto.setMultimedia(obtenerMultimediaPorEmprendimiento(id));

        return dto;
    }

    /**
     * NUEVO: Obtener multimedia asociada a un emprendimiento
     */
    private List<MultimediaDTO> obtenerMultimediaPorEmprendimiento(Integer emprendimientoId) {
        List<EmprendimientoMultimedia> empMultimedia =
                emprendimientoMultimediaRepository.findByEmprendimientoId(emprendimientoId);

        return empMultimedia.stream()
                .map(em -> {
                    MultimediaDTO dto = new MultimediaDTO();
                    dto.setId(em.getMultimedia().getId());
                    dto.setUrlArchivo(em.getMultimedia().getUrlArchivo());
                    dto.setNombreActivo(em.getMultimedia().getNombreActivo());
                    dto.setTipo(em.getTipo());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<EmprendimientoResponseDTO> obtenerEmprendimientosPorUsuario(Usuarios usuario) {
        List<Emprendimientos> lista = emprendimientosRepository.findByUsuarios(usuario);
        return lista.stream().map(emp -> {
            EmprendimientoResponseDTO dto = EmprendimientoMapper.toResponseDTO(emp);
            dto.setCategorias(EmprendimientoMapper.toCategoriaDTOList(
                    emprendimientoCategoriasRepository.findEmprendimientosPorCategoria(emp.getId())
            ));
            dto.setDescripciones(EmprendimientoMapper.toDescripcionDTOList(
                    emprendimientosDescripcionRepository.findByEmprendimientoId(emp.getId())
            ));
            dto.setPresenciasDigitales(EmprendimientoMapper.toPresenciaDigitalDTOList(
                    emprendimientoPresenciaDigitalRepository.findByEmprendimientoId(emp.getId())
            ));
            dto.setMetricas(EmprendimientoMapper.toMetricasDTOList(
                    emprendimientoMetricaRepository.findByEmprendimientoId(emp.getId())
            ));
            dto.setDeclaracionesFinales(EmprendimientoMapper.toDeclaracionesDTOList(
                    emprendimientoDeclaracionesRepository.findByEmprendimientoId(emp.getId())
            ));
            dto.setParticipacionesComunidad(EmprendimientoMapper.toParticipacionDTOList(
                    emprendimientoParticicipacionComunidadRepository.findByEmprendimientoIdFetchOpcion(emp.getId())
            ));
            dto.setInformacionRepresentante(EmprendimientoMapper.toRepresentanteDTO(
                    informacionRepresentanteRepository.findFirstByEmprendimientoId(emp.getId())
            ));
            dto.setMultimedia(obtenerMultimediaPorEmprendimiento(emp.getId()));
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public Page<EmprendimientoResponseDTO> obtenerEmprendimientosFiltrado(String nombre, String tipo, String categoria, String ciudad, Pageable pageable) {
        // Normalizar los parámetros: convertir strings vacíos a null
        String nombreParam = (nombre != null && !nombre.trim().isEmpty()) ? nombre.trim() : null;
        String tipoParam = (tipo != null && !tipo.trim().isEmpty()) ? tipo.trim() : null;
        String categoriaParam = (categoria != null && !categoria.trim().isEmpty()) ? categoria.trim() : null;
        String ciudadParam = (ciudad != null && !ciudad.trim().isEmpty()) ? ciudad.trim() : null;

        Page<Emprendimientos> page = emprendimientosRepository.findByFiltros(nombreParam, tipoParam, categoriaParam, ciudadParam, pageable);
        List<EmprendimientoResponseDTO> dtos = page.getContent().stream()
            .map(EmprendimientoMapper::toResponseDTO)
            .collect(Collectors.toList());
        return new PageImpl<>(dtos, pageable, page.getTotalElements());
    }

    @Override
    public EmprendimientoPorCategoriaDTO obtenerEmprendimientosPorCategoria(Integer categoriaId) {
        Categorias categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con id: " + categoriaId));

        List<EmprendimientoCategorias> emprendimientoCategorias =
                emprendimientoCategoriasRepository.findByCategoriaId(categoriaId);

        List<EmprendimientoSimpleDTO> emprendimientos = emprendimientoCategorias.stream()
                .map(ec -> {
                    Emprendimientos emp = ec.getEmprendimiento();
                    return EmprendimientoSimpleDTO.builder()
                            .id(emp.getId().longValue())
                            .nombreComercial(emp.getNombreComercial())
                            .ciudad(emp.getCiudades().getNombreCiudad())
                            .build();
                })
                .collect(Collectors.toList());

        return EmprendimientoPorCategoriaDTO.builder()
                .categoriaId(categoria.getId())
                .nombreCategoria(categoria.getNombre())
                .emprendimientos(emprendimientos)
                .build();
    }

    @Transactional
    public Emprendimientos crearBorradorEmprendimiento(@Valid EmprendimientoDTO emprendimientoDTO, Usuarios usuario) {
        log.info("Creando borrador de emprendimiento para usuario: {}", usuario.getId());

        Ciudades ciudad = ciudadesRepository.findById(emprendimientoDTO.getCiudad())
                .orElseThrow(() -> new EntityNotFoundException("Ciudad no encontrada con ID: " + emprendimientoDTO.getCiudad()));

        TiposEmprendimientos tipoEmprendimiento = tiposEmprendimientoRepository
                .findById(emprendimientoDTO.getTipoEmprendimientoId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Tipo de emprendimiento no encontrado con ID: " + emprendimientoDTO.getTipoEmprendimientoId()));

        Emprendimientos emprendimiento = new Emprendimientos();
        emprendimiento.setNombreComercial(emprendimientoDTO.getNombreComercialEmprendimiento());
        emprendimiento.setAnioCreacion(emprendimientoDTO.getFechaCreacion());
        emprendimiento.setActivoEmprendimiento(emprendimientoDTO.getEstadoEmpredimiento());
        emprendimiento.setAceptaDatosPublicos(emprendimientoDTO.getDatosPublicos());
        emprendimiento.setFechaCreacion(emprendimientoDTO.getFechaCreacion());
        emprendimiento.setEstadoEmprendimiento(String.valueOf(EstadoEmprendimiento.BORRADOR));
        emprendimiento.setUsuarios(usuario);
        emprendimiento.setCiudades(ciudad);
        emprendimiento.setTiposEmprendimientos(tipoEmprendimiento);

        Emprendimientos guardado = emprendimientosRepository.save(emprendimiento);
        log.info("Borrador creado exitosamente con ID: {}", guardado.getId());

        return guardado;
    }

    // Métodos auxiliares de actualización
    private void actualizarMetricas(Integer id, Emprendimientos emprendimiento, List<EmprendimientoMetricasDTO> nuevas) {
        List<EmprendimientoMetricas> actuales = emprendimientoMetricaRepository.findByEmprendimientoId(id);
        List<EmprendimientoMetricasDTO> nuevasMetricas = nuevas != null ? nuevas : List.of();

        for (EmprendimientoMetricas actual : actuales) {
            boolean existe = nuevasMetricas.stream()
                    .anyMatch(m -> m.getMetricaId().equals(actual.getMetrica().getId()));
            if (!existe) {
                emprendimientoMetricaRepository.delete(actual);
            }
        }

        for (EmprendimientoMetricasDTO nueva : nuevasMetricas) {
            EmprendimientoMetricas actual = actuales.stream()
                    .filter(m -> m.getMetrica().getId().equals(nueva.getMetricaId()))
                    .findFirst()
                    .orElse(null);

            if (actual != null) {
                actual.setValor(nueva.getValor());
                emprendimientoMetricaRepository.save(actual);
            } else {
                MetricasBasicas metrica = tiposMetricasRepository.findById(nueva.getMetricaId()).orElseThrow();
                EmprendimientoMetricas nuevaMetrica = new EmprendimientoMetricas();
                nuevaMetrica.setMetrica(metrica);
                nuevaMetrica.setEmprendimiento(emprendimiento);
                nuevaMetrica.setValor(nueva.getValor());
                emprendimientoMetricaRepository.save(nuevaMetrica);
            }
        }
    }

    private void actualizarPresenciaDigital(Integer id, Emprendimientos emprendimiento, List<EmprendimientoPresenciaDigitalDTO> nuevas) {
        List<TiposPresenciaDigital> actuales = emprendimientoPresenciaDigitalRepository.findByEmprendimientoId(id);
        List<EmprendimientoPresenciaDigitalDTO> nuevasPresencias = nuevas != null ? nuevas : List.of();

        for (TiposPresenciaDigital actual : actuales) {
            boolean existe = nuevasPresencias.stream()
                    .anyMatch(p -> p.getPlataforma().equals(actual.getPlataforma()));
            if (!existe) {
                emprendimientoPresenciaDigitalRepository.delete(actual);
            }
        }

        for (EmprendimientoPresenciaDigitalDTO nueva : nuevasPresencias) {
            TiposPresenciaDigital actual = actuales.stream()
                    .filter(p -> p.getPlataforma().equals(nueva.getPlataforma()))
                    .findFirst()
                    .orElse(null);

            if (actual != null) {
                actual.setDescripcion(nueva.getDescripcion());
                emprendimientoPresenciaDigitalRepository.save(actual);
            } else {
                TiposPresenciaDigital nuevaPres = new TiposPresenciaDigital();
                nuevaPres.setPlataforma(nueva.getPlataforma());
                nuevaPres.setDescripcion(nueva.getDescripcion());
                nuevaPres.setEmprendimiento(emprendimiento);
                emprendimientoPresenciaDigitalRepository.save(nuevaPres);
            }
        }
    }

    private void actualizarParticipacionComunidad(Integer id, Emprendimientos emprendimiento, List<EmprendimientoParticipacionDTO> nuevas) {
        List<EmprendimientoParticipacion> actuales = emprendimientoParticicipacionComunidadRepository.findByEmprendimientoId(id);
        List<EmprendimientoParticipacionDTO> nuevasParticipaciones = nuevas != null ? nuevas : List.of();

        for (EmprendimientoParticipacion actual : actuales) {
            boolean existe = nuevasParticipaciones.stream()
                    .anyMatch(p -> p.getOpcionParticipacionId().equals(actual.getOpcionParticipacion().getId()));
            if (!existe) {
                emprendimientoParticicipacionComunidadRepository.delete(actual);
            }
        }

        for (EmprendimientoParticipacionDTO nueva : nuevasParticipaciones) {
            EmprendimientoParticipacion actual = actuales.stream()
                    .filter(p -> p.getOpcionParticipacion().getId().equals(nueva.getOpcionParticipacionId()))
                    .findFirst()
                    .orElse(null);

            if (actual != null) {
                actual.setRespuesta(nueva.getRespuesta());
                emprendimientoParticicipacionComunidadRepository.save(actual);
            } else {
                OpcionesParticipacionComunidad opcion = opcionesParticipacionComunidadRepository
                        .findById(nueva.getOpcionParticipacionId()).orElseThrow();
                EmprendimientoParticipacion nuevaPart = new EmprendimientoParticipacion();
                nuevaPart.setOpcionParticipacion(opcion);
                nuevaPart.setEmprendimiento(emprendimiento);
                nuevaPart.setRespuesta(nueva.getRespuesta());
                emprendimientoParticicipacionComunidadRepository.save(nuevaPart);
            }
        }
    }

    private void actualizarDeclaraciones(Integer id, Emprendimientos emprendimiento, List<EmprendimientoDeclaracionesDTO> nuevas) {
        List<EmprendimientoDeclaraciones> actuales = emprendimientoDeclaracionesRepository.findByEmprendimientoId(id);
        List<EmprendimientoDeclaracionesDTO> nuevasDeclaraciones = nuevas != null ? nuevas : List.of();

        for (EmprendimientoDeclaraciones actual : actuales) {
            boolean existe = nuevasDeclaraciones.stream()
                    .anyMatch(d -> d.getDeclaracionId().equals(actual.getDeclaracion().getId()));
            if (!existe) {
                emprendimientoDeclaracionesRepository.delete(actual);
            }
        }

        for (EmprendimientoDeclaracionesDTO nueva : nuevasDeclaraciones) {
            EmprendimientoDeclaraciones actual = actuales.stream()
                    .filter(d -> d.getDeclaracion().getId().equals(nueva.getDeclaracionId()))
                    .findFirst()
                    .orElse(null);

            if (actual != null) {
                actual.setAceptada(nueva.getAceptada());
                actual.setNombreFirma(nueva.getNombreFirma());
                actual.setFechaAceptacion(nueva.getFechaAceptacion());
                emprendimientoDeclaracionesRepository.save(actual);
            } else {
                DeclaracionesFinales declaracion = declaracionesFinalesRepository
                        .findById(nueva.getDeclaracionId()).orElseThrow();
                EmprendimientoDeclaraciones nuevaDecl = new EmprendimientoDeclaraciones();
                nuevaDecl.setDeclaracion(declaracion);
                nuevaDecl.setEmprendimiento(emprendimiento);
                nuevaDecl.setAceptada(nueva.getAceptada());
                nuevaDecl.setNombreFirma(nueva.getNombreFirma());
                nuevaDecl.setFechaAceptacion(nueva.getFechaAceptacion());
                emprendimientoDeclaracionesRepository.save(nuevaDecl);
            }
        }
    }

    private void agregarDescripcionEmprendimiento(Emprendimientos emprendimiento, List<EmprendimientoDescripcionDTO> lsDescripcion) {
        if (CollectionUtils.isEmpty(lsDescripcion)) {
            log.debug("No hay descripciones para agregar");
            return;
        }

        log.debug("Agregando {} descripciones al emprendimiento: {}", lsDescripcion.size(), emprendimiento.getId());

        List<TiposDescripcionEmprendimiento> descripciones = lsDescripcion.stream()
                .map(dto -> {
                    TiposDescripcionEmprendimiento descripcion = new TiposDescripcionEmprendimiento();
                    descripcion.setTipoDescripcion(dto.getTipoDescripcion());
                    descripcion.setDescripcion(dto.getDescripcion());
                    descripcion.setMaxCaracteres(dto.getMaxCaracteres());
                    descripcion.setObligatorio(dto.getObligatorio());
                    descripcion.setEmprendimiento(emprendimiento);
                    return descripcion;
                })
                .collect(Collectors.toList());

        emprendimientosDescripcionRepository.saveAll(descripciones);
    }

    private void agregarPresenciaDigitalEmprendimiento(Emprendimientos emprendimiento, List<EmprendimientoPresenciaDigitalDTO> lsPresenciaDigital) {
        if (CollectionUtils.isEmpty(lsPresenciaDigital)) {
            log.debug("No hay presencia digital para agregar");
            return;
        }

        log.debug("Agregando {} presencias digitales al emprendimiento: {}", lsPresenciaDigital.size(), emprendimiento.getId());

        List<TiposPresenciaDigital> presencias = lsPresenciaDigital.stream()
                .map(dto -> {
                    TiposPresenciaDigital presencia = new TiposPresenciaDigital();
                    presencia.setDescripcion(dto.getDescripcion());
                    presencia.setPlataforma(dto.getPlataforma());
                    presencia.setEmprendimiento(emprendimiento);
                    return presencia;
                })
                .collect(Collectors.toList());

        emprendimientoPresenciaDigitalRepository.saveAll(presencias);
    }

    private void agregarMetricasEmprendimiento(Emprendimientos emprendimiento, List<EmprendimientoMetricasDTO> lsMetricas) {
        if (CollectionUtils.isEmpty(lsMetricas)) {
            log.debug("No hay métricas para agregar");
            return;
        }

        log.debug("Agregando {} métricas al emprendimiento: {}", lsMetricas.size(), emprendimiento.getId());

        List<EmprendimientoMetricas> metricas = lsMetricas.stream()
                .map(dto -> {
                    MetricasBasicas metrica = tiposMetricasRepository.findById(dto.getMetricaId())
                            .orElseThrow(() -> new EntityNotFoundException(
                                    "Métrica no encontrada con ID: " + dto.getMetricaId()));

                    EmprendimientoMetricas metricaEmp = new EmprendimientoMetricas();
                    metricaEmp.setMetrica(metrica);
                    metricaEmp.setEmprendimiento(emprendimiento);
                    metricaEmp.setValor(dto.getValor());
                    return metricaEmp;
                })
                .collect(Collectors.toList());

        emprendimientoMetricaRepository.saveAll(metricas);
    }

    private void agregarParticipacionComunidad(Emprendimientos emprendimiento, List<EmprendimientoParticipacionDTO> lsParticipacionComunidad) {
        if (CollectionUtils.isEmpty(lsParticipacionComunidad)) {
            log.debug("No hay participaciones en comunidad para agregar");
            return;
        }

        log.debug("Agregando {} participaciones en comunidad al emprendimiento: {}",
                lsParticipacionComunidad.size(), emprendimiento.getId());

        List<EmprendimientoParticipacion> participaciones = lsParticipacionComunidad.stream()
                .map(dto -> {
                    OpcionesParticipacionComunidad opcion = opcionesParticipacionComunidadRepository
                            .findById(dto.getOpcionParticipacionId())
                            .orElseThrow(() -> new EntityNotFoundException(
                                    "Opción de participación no encontrada con ID: " + dto.getOpcionParticipacionId()));

                    EmprendimientoParticipacion participacion = new EmprendimientoParticipacion();
                    participacion.setOpcionParticipacion(opcion);
                    participacion.setEmprendimiento(emprendimiento);
                    participacion.setRespuesta(dto.getRespuesta());
                    return participacion;
                })
                .collect(Collectors.toList());

        emprendimientoParticicipacionComunidadRepository.saveAll(participaciones);
    }

    // Al final del método agregarDeclaracionesFinales
    private void agregarDeclaracionesFinales(Emprendimientos emprendimiento, List<EmprendimientoDeclaracionesDTO> lsDeclaracionesFinales) {
        if (CollectionUtils.isEmpty(lsDeclaracionesFinales)) {
            log.debug("No hay declaraciones finales para agregar");
            return;
        }

        log.debug("Agregando {} declaraciones finales al emprendimiento: {}",
                lsDeclaracionesFinales.size(), emprendimiento.getId());

        List<EmprendimientoDeclaraciones> declaraciones = lsDeclaracionesFinales.stream()
                .map(dto -> {
                    DeclaracionesFinales declaracion = declaracionesFinalesRepository
                            .findById(dto.getDeclaracionId())
                            .orElseThrow(() -> new EntityNotFoundException(
                                    "Declaración final no encontrada con ID: " + dto.getDeclaracionId()));

                    EmprendimientoDeclaraciones declaracionEmp = new EmprendimientoDeclaraciones();
                    declaracionEmp.setAceptada(dto.getAceptada());
                    declaracionEmp.setNombreFirma(dto.getNombreFirma());
                    declaracionEmp.setFechaAceptacion(dto.getFechaAceptacion());
                    declaracionEmp.setEmprendimiento(emprendimiento);
                    declaracionEmp.setDeclaracion(declaracion);
                    return declaracionEmp;
                })
                .collect(Collectors.toList());

        emprendimientoDeclaracionesRepository.saveAll(declaraciones);
    }


    @Override
    public void inactivarEmprendimiento(Integer id) throws Exception {
        Emprendimientos emprendimientos = emprendimientosRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        emprendimientos.setActivoEmprendimiento(false);
        emprendimientosRepository.save(emprendimientos);
    }

    @Override
    public void activarEmprendimiento(Integer id) throws Exception {
        Emprendimientos emprendimientos = emprendimientosRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        emprendimientos.setActivoEmprendimiento(true);
        emprendimientosRepository.save(emprendimientos);
    }

}

