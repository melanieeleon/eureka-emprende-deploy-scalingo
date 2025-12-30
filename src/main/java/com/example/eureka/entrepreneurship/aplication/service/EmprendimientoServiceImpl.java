package com.example.eureka.entrepreneurship.aplication.service;

import com.example.eureka.auth.domain.Usuarios;
import com.example.eureka.auth.port.out.IUserRepository;
import com.example.eureka.autoevaluacion.port.out.IAutoevaluacionRepository;
import com.example.eureka.entrepreneurship.domain.model.*;
import com.example.eureka.entrepreneurship.infrastructure.dto.publico.EmprendimientoListaPublicoDTO;
import com.example.eureka.entrepreneurship.infrastructure.dto.publico.MiniEmprendimientoDTO;
import com.example.eureka.entrepreneurship.aplication.service.SolicitudAprobacionService;
import com.example.eureka.entrepreneurship.infrastructure.dto.response.*;
import com.example.eureka.entrepreneurship.infrastructure.dto.shared.*;
import com.example.eureka.entrepreneurship.port.in.MultimediaService;
import com.example.eureka.entrepreneurship.port.out.*;
import com.example.eureka.formulario.domain.model.Pregunta;
import com.example.eureka.general.domain.model.*;
import com.example.eureka.general.port.out.*;
import com.example.eureka.entrepreneurship.infrastructure.dto.request.EmprendimientoRequestDTO;
import com.example.eureka.metricas.domain.MetricasBasicas;
import com.example.eureka.metricas.domain.MetricasGenerales;
import com.example.eureka.metricas.domain.MetricasPregunta;
import com.example.eureka.metricas.port.out.IMetricasGeneralesRepository;
import com.example.eureka.metricas.port.out.IMetricasPreguntaRepository;
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
import java.time.LocalDateTime;
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
    private final IOpcionesPersonaJuridicaRepository opcionesPersonaJuridicaRepository;
    // NUEVAS DEPENDENCIAS PARA MULTIMEDIA
    private final IMultimediaRepository multimediaRepository;
    private final IEmprendimientoMultimediaRepository emprendimientoMultimediaRepository;
    private final FileStorageService fileStorageService;
    private final ICategoriaRepository categoriasRepository;
    private final MultimediaService multimediaService;


    private final IMetricasGeneralesRepository metricasGeneralesRepository;
    private final IAutoevaluacionRepository autoevaluacionRepository;
    private final IMetricasPreguntaRepository metricasPreguntaRepository;
    private final IPreguntaRepository preguntaRepository;

    private final IDescripcionesRepository descripcionesRepository;


    @Override
    @Transactional
    public SolicitudAprobacion enviarParaAprobacion(Integer emprendimientoId, Usuarios usuario) {
        log.info("Enviando emprendimiento {} para aprobación", emprendimientoId);
        EmprendimientoDetallesDTO datosCompletos = solicitudAprobacionService
                .capturarEstadoCompleto(emprendimientoId);
        return solicitudAprobacionService.crearSolicitud(emprendimientoId, datosCompletos, usuario);
    }

    @Override
    public VistaEmprendedorDTO obtenerVistaEmprendedor(Integer emprendimientoId) {
        return solicitudAprobacionService.obtenerVistaEmprendedor(emprendimientoId);
    }

    @Override
    @Transactional
    public SolicitudAprobacion guardarPropuestaEmprendimiento(
            Integer emprendimientoId,
            EmprendimientoDetallesDTO dto,
            Usuarios usuario) {

        return solicitudAprobacionService.guardarPropuesta(emprendimientoId, dto, usuario);
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

        if (emprendimientoRequestDTO.getInformacionRepresentante() != null) {
            guardarInformacionRepresentante(
                    emprendimiento,
                    emprendimientoRequestDTO.getInformacionRepresentante()
            );
        }

        if (!CollectionUtils.isEmpty(emprendimientoRequestDTO.getImagenes())) {
            multimediaService.agregarImagenes(
                    emprendimiento.getId(),
                    emprendimientoRequestDTO.getImagenes(),
                    emprendimientoRequestDTO.getTiposMultimedia()
            );
        }


        // Agregar todas las relaciones solo si es CREAR (no borrador)
        //if ("CREAR".equals(emprendimientoRequestDTO.getTipoAccion())) {
         if(null != emprendimientoRequestDTO.getCategorias()) {
             agregarCategoriaEmprendimiento(emprendimiento, emprendimientoRequestDTO.getCategorias());
         }
         if(null != emprendimientoRequestDTO.getDescripciones()){
             agregarDescripcionEmprendimiento(emprendimiento, emprendimientoRequestDTO.getDescripciones());
         }

         if(null != emprendimientoRequestDTO.getMetricas()){
             agregarMetricasEmprendimiento(emprendimiento, emprendimientoRequestDTO.getMetricas());
         }

         if(null != emprendimientoRequestDTO.getPresenciasDigitales()){
             agregarPresenciaDigitalEmprendimiento(emprendimiento, emprendimientoRequestDTO.getPresenciasDigitales());
         }

         if(null != emprendimientoRequestDTO.getParticipacionesComunidad()){
             agregarParticipacionComunidad(emprendimiento, emprendimientoRequestDTO.getParticipacionesComunidad());
         }

         if(null != emprendimientoRequestDTO.getDeclaracionesFinales()){
             agregarDeclaracionesFinales(emprendimiento, emprendimientoRequestDTO.getDeclaracionesFinales());
         }

        //}

        log.info("Emprendimiento creado exitosamente with ID: {}", emprendimiento.getId());
        return emprendimiento.getId();
    }


    private void guardarInformacionRepresentante(Emprendimientos emprendimiento,
                                                 InformacionRepresentanteDTO dto) {

        InformacionRepresentante entidad = new InformacionRepresentante();
        entidad.setNombre(dto.getNombre());
        entidad.setApellido(dto.getApellido());
        entidad.setCorreoCorporativo(dto.getCorreoCorporativo());
        entidad.setCorreoPersonal(dto.getCorreoPersonal());
        entidad.setTelefono(dto.getTelefono());
        entidad.setIdentificacion(dto.getIdentificacion());
        entidad.setCarrera(dto.getCarrera());
        entidad.setSemestre(dto.getSemestre());
        if (dto.getFechaGraduacion() != null) {
            entidad.setFechaGraduacion(dto.getFechaGraduacion());
        }
        entidad.setTieneParientesUees(dto.getTieneParientesUees());
        entidad.setNombrePariente(dto.getNombrePariente());
        entidad.setAreaPariente(dto.getAreaPariente());
        entidad.setIntegrantesEquipo(dto.getIntegrantesEquipo());
        entidad.setEmprendimiento(emprendimiento);

        informacionRepresentanteRepository.save(entidad);
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
        if (emprendimientoDTO.getTipoPersonaJuridicaId() != null) {
            OpcionesPersonaJuridica personaJuridica = opcionesPersonaJuridicaRepository
                    .findById(emprendimientoDTO.getTipoPersonaJuridicaId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Tipo persona jurídica no encontrado con ID: " + emprendimientoDTO.getTipoPersonaJuridicaId()));
            emprendimiento.setTipoPersonaJuridica(personaJuridica);
        }
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

        if (!CollectionUtils.isEmpty(emprendimientoRequestDTO.getImagenes())) {
            multimediaService.agregarImagenes(
                    emprendimiento.getId(),
                    emprendimientoRequestDTO.getImagenes(),
                    emprendimientoRequestDTO.getTiposMultimedia()
            );
        }

        // Actualizar relaciones
        if(null != emprendimientoRequestDTO.getCategorias()){
            emprendimientoCategoriasRepository.deleteEmprendimientoCategoriasByEmprendimientoId(id);
            agregarCategoriaEmprendimiento(emprendimiento, emprendimientoRequestDTO.getCategorias());
        }

        if (emprendimientoRequestDTO.getInformacionRepresentante() != null) {
            actualizarInformacionRepresentante(emprendimiento, emprendimientoRequestDTO.getInformacionRepresentante());
        }
        // ... (resto del código de actualización sin cambios)
        if(null != emprendimientoRequestDTO.getDescripciones()){
            actualizarDescripciones(id, emprendimiento, emprendimientoRequestDTO.getDescripciones());
        }

        if(null != emprendimientoRequestDTO.getMetricas()){
            actualizarMetricas(id, emprendimiento, emprendimientoRequestDTO.getMetricas());
        }

        if(null != emprendimientoRequestDTO.getPresenciasDigitales()){
            actualizarPresenciaDigital(id, emprendimiento, emprendimientoRequestDTO.getPresenciasDigitales());
        }

        if(null != emprendimientoRequestDTO.getParticipacionesComunidad()){
            actualizarParticipacionComunidad(id, emprendimiento, emprendimientoRequestDTO.getParticipacionesComunidad());
        }

        if(null != emprendimientoRequestDTO.getDeclaracionesFinales()){
            actualizarDeclaraciones(id, emprendimiento, emprendimientoRequestDTO.getDeclaracionesFinales());
        }

        return obtenerEmprendimientoCompletoPorId(id);
    }

    private void actualizarInformacionRepresentante(Emprendimientos emprendimiento,
                                                    InformacionRepresentanteDTO dto) {

        InformacionRepresentante entidad =
                representanteInformacionRepository
                        .findFirstByEmprendimientoId(emprendimiento.getId());

        if (entidad == null) {
            entidad = new InformacionRepresentante();
            entidad.setEmprendimiento(emprendimiento);
        }

        entidad.setNombre(dto.getNombre());
        entidad.setApellido(dto.getApellido());
        entidad.setCorreoCorporativo(dto.getCorreoCorporativo());
        entidad.setCorreoPersonal(dto.getCorreoPersonal());
        entidad.setTelefono(dto.getTelefono());
        entidad.setIdentificacion(dto.getIdentificacion());
        entidad.setCarrera(dto.getCarrera());
        entidad.setSemestre(dto.getSemestre());
        entidad.setFechaGraduacion(dto.getFechaGraduacion()); // si ambos son LocalDateTime
        entidad.setTieneParientesUees(dto.getTieneParientesUees());
        entidad.setNombrePariente(dto.getNombrePariente());
        entidad.setAreaPariente(dto.getAreaPariente());
        entidad.setIntegrantesEquipo(dto.getIntegrantesEquipo());

        representanteInformacionRepository.save(entidad);
    }


    // Métodos auxiliares para actualización (extraídos para mejor organización)
    private void actualizarDescripciones(Integer id, Emprendimientos emprendimiento, List<EmprendimientoDescripcionDTO> nuevas) {
        List<DescripcionEmprendimiento> actuales = emprendimientosDescripcionRepository.findByEmprendimientoId(id);
        List<EmprendimientoDescripcionDTO> nuevasDescripciones = nuevas != null ? nuevas : List.of();

        for (DescripcionEmprendimiento actual : actuales) {

            boolean existe = nuevasDescripciones.stream()
                    .anyMatch(d -> d.getRespuesta().equals(actual.getRespuesta()));
            if (!existe) {
                emprendimientosDescripcionRepository.delete(actual);
            }
        }

        for (EmprendimientoDescripcionDTO nueva : nuevasDescripciones) {
            Descripciones de = descripcionesRepository.findById(nueva.getIdDescripcion()).orElse(null);
            DescripcionEmprendimiento actual = emprendimientosDescripcionRepository.findByEmprendimientoAndDescripciones(emprendimiento, de);
            /*DescripcionEmprendimiento actual = actuales.stream()
                    .filter(d -> d.getRespuesta().equals(nueva.getRespuesta()))
                    .findFirst()
                    .orElse(null);*/

            if (actual != null) {
                actual.setRespuesta(nueva.getRespuesta());
                emprendimientosDescripcionRepository.save(actual);
            } else {
                DescripcionEmprendimiento nuevaDesc = new DescripcionEmprendimiento();
                nuevaDesc.setRespuesta(nueva.getRespuesta());
                nuevaDesc.setDescripciones(de);
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
        List<Emprendimientos> lista = emprendimientosRepository.findByEstadoEmprendimiento("PUBLICADO");

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
                .filter(d -> "1".equals(d.getRespuesta()))
                .collect(Collectors.toMap(
                        d -> d.getEmprendimiento().getId(),
                        DescripcionEmprendimiento::getRespuesta,
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
            lista = emprendimientosRepository.findByUsuariosAndEstadoEmprendimientoEquals(usuario,"PUBLICADO");
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
    public EmprendimientoPublicoDTO obtenerEmprendimientoPublicoPorId(Integer id) {
        Emprendimientos e = emprendimientosRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Emprendimiento no encontrado con id: " + id));

        // 1) Datos básicos
        EmprendimientoPublicoDTO dto = EmprendimientoMapper.toPublicoDTO(e);

        // 2) Categorías (usando la tabla intermedia)
        dto.setCategorias(
                emprendimientoCategoriasRepository.findByEmprendimientoIdWithCategoria(id)
                        .stream()
                        .map(rel -> new CategoriaListadoDTO(
                                rel.getCategoria().getId().intValue(),
                                rel.getCategoria().getNombre()
                        ))
                        .toList()
        );

        // 3) Descripciones
        dto.setDescripciones(
                emprendimientosDescripcionRepository.findByEmprendimientoId(id)
                        .stream()
                        .map(EmprendimientoMapper::mapDescripcionToDTO)   // asegúrate que sea public static
                        .toList()
        );

        // 4) Presencias digitales
        dto.setPresenciasDigitales(
                emprendimientoPresenciaDigitalRepository.findByEmprendimientoId(id)
                        .stream()
                        .map(EmprendimientoMapper::toPresenciaDigitalDTO)  // también public static
                        .toList()
        );

        // 5) Multimedia (solo nombre + url)
        dto.setMultimedia(
                emprendimientoMultimediaRepository.findByEmprendimientoId(id)
                        .stream()
                        .map(m -> new MultimediaListadoDTO(
                                m.getMultimedia().getId(),
                                m.getMultimedia().getNombreActivo(),
                                m.getMultimedia().getUrlArchivo()
                        ))
                        .toList()
        );
    return dto;}




        @Override
    @Transactional
    public EmprendimientoResponseDTO obtenerEmprendimientoCompletoPorId(Integer id) {
        Emprendimientos emprendimiento = emprendimientosRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Emprendimiento no encontrado con id: " + id));

        EmprendimientoResponseDTO dto = EmprendimientoMapper.toResponseDTO(emprendimiento);

        dto.setCategorias(EmprendimientoMapper.toCategoriaDTOList(
                emprendimientoCategoriasRepository.findByEmprendimientoIdWithCategoria(id)
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

        // Multimedia
        dto.setMultimedia(obtenerMultimediaPorEmprendimiento(id));

        // Solo métricas de vistas
        try {
            MetricasGenerales metricasGenerales =
                    metricasGeneralesRepository.findByEmprendimientos(emprendimiento).orElse(null);

            if (metricasGenerales == null) {
                metricasGenerales = new MetricasGenerales();
                metricasGenerales.setEmprendimientos(emprendimiento);
                metricasGenerales.setVistas(1);
                metricasGenerales.setFechaRegistro(LocalDateTime.now());
            } else {
                metricasGenerales.setVistas(metricasGenerales.getVistas() + 1);
            }

            metricasGeneralesRepository.save(metricasGenerales);
        } catch (Exception e) {
            log.error("ERROR AL GUARDAR MÉTRICA DE VISTAS DEL EMPRENDIMIENTO {}", emprendimiento.getId(), e);
        }

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
    public Page<EmprendimientoListadoResponseDTO> obtenerEmprendimientosPorUsuario(
            Usuarios usuario, Pageable pageable) {

        Page<EmprendimientoListadoResponseDTO> page =
                emprendimientosRepository.findByUsuarioListado(usuario, pageable);

        page.getContent().forEach(dto -> {
            Integer idEmprendimiento = dto.getIdEmprendimiento();

            var ecs = emprendimientoCategoriasRepository.findByEmprendimientoIdWithCategoria(idEmprendimiento);
            dto.setCategorias(
                    ecs.stream()
                            .map(ec -> new CategoriaListadoDTO(
                                    ec.getCategoria().getId(),
                                    ec.getCategoria().getNombre()
                            ))
                            .toList()
            );

            var ems = emprendimientoMultimediaRepository.findByEmprendimientoId(idEmprendimiento);
            dto.setMultimedia(
                    ems.stream()
                            .map(em -> new MultimediaListadoDTO(
                                    em.getMultimedia().getId(),
                                    em.getMultimedia().getNombreActivo(),
                                    em.getMultimedia().getUrlArchivo()
                            ))
                            .toList()
            );
        });

        return page;
    }



    @Override
    public Page<EmprendimientoListadoResponseDTO> obtenerEmprendimientosFiltrado(
            String nombre, String tipo, String subtipo, String categoria, String ciudad, Pageable pageable) {

        String nombreParam = (nombre != null && !nombre.trim().isEmpty()) ? nombre.trim() : null;
        String tipoParam = (tipo != null && !tipo.trim().isEmpty()) ? tipo.trim() : null;
        String subtipoParam  = (subtipo  != null && !subtipo.trim().isEmpty())  ? subtipo.trim()  : null;
        String categoriaParam = (categoria != null && !categoria.trim().isEmpty()) ? categoria.trim() : null;
        String ciudadParam = (ciudad != null && !ciudad.trim().isEmpty()) ? ciudad.trim() : null;

        Page<EmprendimientoListadoResponseDTO> page =
                emprendimientosRepository.findByFiltrosListado(
                        nombreParam, tipoParam,subtipoParam, categoriaParam, ciudadParam, pageable);

        page.getContent().forEach(dto -> {
            Integer idEmprendimiento = dto.getIdEmprendimiento();

            // Categorías
            var ecs = emprendimientoCategoriasRepository.findByEmprendimientoIdWithCategoria(idEmprendimiento);
            dto.setCategorias(
                    ecs.stream()
                            .map(ec -> new CategoriaListadoDTO(
                                    ec.getCategoria().getId(),
                                    ec.getCategoria().getNombre()
                            ))
                            .toList()
            );

            // Multimedia
            var ems = emprendimientoMultimediaRepository.findByEmprendimientoId(idEmprendimiento);
            dto.setMultimedia(
                    ems.stream()
                            .map(em -> new MultimediaListadoDTO(
                                    em.getMultimedia().getId(),
                                    em.getMultimedia().getNombreActivo(),
                                    em.getMultimedia().getUrlArchivo()
                            ))
                            .toList()
            );
        });

        return page;
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

        List<DescripcionEmprendimiento> descripciones = lsDescripcion.stream()
                .map(dto -> {
                    Descripciones de = descripcionesRepository.findById(dto.getIdDescripcion()).orElseThrow();
                    DescripcionEmprendimiento descripcion = new DescripcionEmprendimiento();
                    descripcion.setDescripciones(de);
                    descripcion.setRespuesta(dto.getRespuesta());
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
        Emprendimientos emprendimientos = emprendimientosRepository.findById(id).orElseThrow(() -> new EntityNotFoundException());
        emprendimientos.setActivoEmprendimiento(false);
        emprendimientosRepository.save(emprendimientos);
    }

    @Override
    public void activarEmprendimiento(Integer id) throws Exception {
        Emprendimientos emprendimientos = emprendimientosRepository.findById(id).orElseThrow(() -> new EntityNotFoundException());
        emprendimientos.setActivoEmprendimiento(true);
        emprendimientosRepository.save(emprendimientos);
    }

}

