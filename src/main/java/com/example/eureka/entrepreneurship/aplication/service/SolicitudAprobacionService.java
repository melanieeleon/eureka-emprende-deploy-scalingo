package com.example.eureka.entrepreneurship.aplication.service;

import com.example.eureka.auth.domain.Usuarios;
import com.example.eureka.auth.port.out.IUserRepository;
import com.example.eureka.entrepreneurship.domain.model.*;
import com.example.eureka.entrepreneurship.infrastructure.dto.response.CategoriaListadoDTO;
import com.example.eureka.entrepreneurship.infrastructure.dto.response.EmprendimientoDetallesDTO;
import com.example.eureka.entrepreneurship.infrastructure.dto.response.MultimediaListadoDTO;
import com.example.eureka.entrepreneurship.infrastructure.dto.response.SolicitudAprobacionListadoDTO;
import com.example.eureka.entrepreneurship.infrastructure.dto.shared.*;
import com.example.eureka.entrepreneurship.infrastructure.mappers.EmprendimientoMapper;
import com.example.eureka.entrepreneurship.port.out.*;
import com.example.eureka.general.domain.model.*;
import com.example.eureka.general.infrastructure.dto.CiudadDTO;
import com.example.eureka.general.infrastructure.dto.ProvinciaDTO;
import com.example.eureka.general.port.out.*;
import com.example.eureka.metricas.domain.MetricasBasicas;
import com.example.eureka.shared.enums.EstadoEmprendimiento;
import com.example.eureka.general.infrastructure.dto.CategoriasDTO;
import com.example.eureka.notificacion.port.in.NotificacionService;
import com.example.eureka.shared.util.PageResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SolicitudAprobacionService {

    private final ISolicitudAprobacionRepository solicitudRepository;
    private final IHistorialRevisionRepository historialRepository;
    private final IEmprendimientosRepository emprendimientosRepository;
    private final ICategoriasRepository categoriasRepository;
    private final IEmprendimientoCategoriasRepository emprendimientoCategoriasRepository;
    private final IEmprendimientosDescripcionRepository emprendimientosDescripcionRepository;
    private final IEmprendimientoPresenciaDigitalRepository emprendimientoPresenciaDigitalRepository;
    private final IEmprendimientoMetricaRepository emprendimientoMetricaRepository;
    private final IEmprendimientoDeclaracionesRepository emprendimientoDeclaracionesRepository;
    private final IEmprendimientoParticicipacionComunidadRepository emprendimientoParticicipacionComunidadRepository;
    private final IRepresentanteInformacionRepository informacionRepresentanteRepository;
    private final IEmprendimientoMultimediaRepository  emprendimientoMultimediaRepository;
    private final ICiudadesRepository ciudadesRepository;
    private final ITiposEmprendimientoRepository tiposEmprendimientoRepository;
    private final ITiposMetricasRepository tiposMetricasRepository;
    private final IOpcionesParticipacionComunidadRepository opcionesParticipacionComunidadRepository;
    private final IDeclaracionesFinalesRepository declaracionesFinalesRepository;
    private final ObjectMapper objectMapper;
    private final NotificacionService notificacionService;
    private final IUserRepository userRepository;
    private final IDescripcionesRepository descripcionesRepository;

    /**
     * Captura el estado completo actual del emprendimiento (todas las relaciones)
     */
    public EmprendimientoDetallesDTO capturarEstadoCompleto(Integer emprendimientoId) {
        log.debug("Capturando estado completo del emprendimiento: {}", emprendimientoId);

        Emprendimientos emp = emprendimientosRepository.findById(emprendimientoId)
                .orElseThrow(() -> new EntityNotFoundException("Emprendimiento no encontrado"));

        EmprendimientoDetallesDTO dto = new EmprendimientoDetallesDTO();

        // Datos básicos
        dto.setNombreComercial(emp.getNombreComercial());
        dto.setAnioCreacion(emp.getAnioCreacion());
        dto.setActivoEmprendimiento(emp.getActivoEmprendimiento());
        dto.setAceptaDatosPublicos(emp.getAceptaDatosPublicos());
        dto.setTipoEmprendimientoId(
                emp.getTiposEmprendimientos() != null ? emp.getTiposEmprendimientos().getId() : null
        );
        dto.setTipoEmprendimiento(
                emp.getTiposEmprendimientos() != null ? emp.getTiposEmprendimientos().getSubTipo() : null
        );

        if (emp.getTipoPersonaJuridica() != null) {
            dto.setTipoPersonaJuridicaId(emp.getTipoPersonaJuridica().getId().intValue());
            dto.setTipoPersonaJuridicaDescripcion(emp.getTipoPersonaJuridica().getDescripcion());
        }

        dto.setFechaCreacion(emp.getFechaCreacion());
        dto.setFechaActualizacion(emp.getFechaActualizacion());

        // Ciudad -> CiudadDTO
        if (emp.getCiudades() != null) {
            CiudadDTO ciudadDTO = new CiudadDTO();
            ciudadDTO.setId(emp.getCiudades().getId());
            ciudadDTO.setNombreCiudad(emp.getCiudades().getNombreCiudad());

            ProvinciaDTO provDTO = new ProvinciaDTO();
            provDTO.setId(emp.getCiudades().getProvincias().getId());
            provDTO.setNombre(emp.getCiudades().getProvincias().getNombre());
            provDTO.setActivo(emp.getCiudades().getProvincias().getActivo());

            ciudadDTO.setProvincia(provDTO);
            dto.setCiudad(ciudadDTO);
        }

        // Información del representante
        InformacionRepresentante rep =
                informacionRepresentanteRepository.findFirstByEmprendimientoId(emprendimientoId);
        if (rep != null) {
            dto.setInformacionRepresentante(mapRepresentanteToDTO(rep));
        }

        // Categorías -> List<CategoriaListadoDTO>
        List<EmprendimientoCategorias> categorias =
                emprendimientoCategoriasRepository.findByEmprendimientoIdWithCategoria(emprendimientoId);
        dto.setCategorias(
                categorias.stream()
                        .map(ec -> new CategoriaListadoDTO(
                                ec.getCategoria().getId(),
                                ec.getCategoria().getNombre()
                        ))
                        .collect(Collectors.toList())
        );

        // Descripciones
        List<DescripcionEmprendimiento> descripciones =
                emprendimientosDescripcionRepository.findByEmprendimientoId(emprendimientoId);
        dto.setDescripciones(
                descripciones.stream()
                        .map(this::mapDescripcionToDTO)   // aquí puedes extender el DTO si quieres más campos
                        .collect(Collectors.toList())
        );

        // Métricas
        List<EmprendimientoMetricas> metricas =
                emprendimientoMetricaRepository.findByEmprendimientoId(emprendimientoId);
        dto.setMetricas(
                metricas.stream()
                        .map(this::mapMetricaToDTO)
                        .collect(Collectors.toList())
        );

        // Presencia digital
        List<TiposPresenciaDigital> presencias =
                emprendimientoPresenciaDigitalRepository.findByEmprendimientoId(emprendimientoId);
        dto.setPresenciasDigitales(
                presencias.stream()
                        .map(this::mapPresenciaDigitalToDTO)
                        .collect(Collectors.toList())
        );

        // Declaraciones
        List<EmprendimientoDeclaraciones> declaraciones =
                emprendimientoDeclaracionesRepository.findByEmprendimientoId(emprendimientoId);
        dto.setDeclaracionesFinales(
                declaraciones.stream()
                        .map(this::mapDeclaracionToDTO)
                        .collect(Collectors.toList())
        );

        // Participación comunidad
        List<EmprendimientoParticipacion> participaciones =
                emprendimientoParticicipacionComunidadRepository.findByEmprendimientoId(emprendimientoId);
        dto.setParticipacionesComunidad(
                participaciones.stream()
                        .map(this::mapParticipacionToDTO)
                        .collect(Collectors.toList())
        );

        // Multimedia -> List<MultimediaListadoDTO>
        List<EmprendimientoMultimedia> multimediaList =
                emprendimientoMultimediaRepository.findByEmprendimientoId(emprendimientoId);
        dto.setMultimedia(
                multimediaList.stream()
                        .map(em -> new MultimediaListadoDTO(
                                em.getMultimedia().getId(),
                                em.getMultimedia().getNombreActivo(),
                                em.getMultimedia().getUrlArchivo()
                        ))
                        .collect(Collectors.toList())
        );

        return dto;
    }


    /**
     * Crear solicitud con todos los datos propuestos
     */
    @Transactional
    public SolicitudAprobacion crearSolicitud(
            Integer emprendimientoId,
            EmprendimientoDetallesDTO datosCompletos,
            Usuarios usuario) {

        log.info("Creando solicitud para emprendimiento: {}", emprendimientoId);

        Emprendimientos emprendimiento = emprendimientosRepository.findById(emprendimientoId)
                .orElseThrow(() -> new EntityNotFoundException("Emprendimiento no encontrado"));

        // Verificar que no haya solicitud activa
        Optional<SolicitudAprobacion> solicitudExistente = solicitudRepository
                .findSolicitudActivaByEmprendimientoId(emprendimientoId);

        if (solicitudExistente.isPresent()) {
            throw new IllegalStateException("Ya existe una solicitud activa para este emprendimiento");
        }

        // Determinar tipo de solicitud
        SolicitudAprobacion.TipoSolicitud tipoSolicitud;
        EmprendimientoDetallesDTO datosOriginales = null;

        String estadoActual = emprendimiento.getEstadoEmprendimiento();

        if (EstadoEmprendimiento.PENDIENTE_APROBACION.name().equals(estadoActual)) {
            tipoSolicitud = SolicitudAprobacion.TipoSolicitud.CREACION;
        } else if (EstadoEmprendimiento.PUBLICADO.name().equals(estadoActual)) {
            tipoSolicitud = SolicitudAprobacion.TipoSolicitud.ACTUALIZACION;
            // Capturar estado original para comparación
            datosOriginales = capturarEstadoCompleto(emprendimientoId);
        } else {
            throw new IllegalStateException("El emprendimiento debe estar en estado PENDIENTE_APROBACION o PUBLICADO");
        }

        // Crear solicitud
        SolicitudAprobacion solicitud = new SolicitudAprobacion();
        solicitud.setEmprendimiento(emprendimiento);
        solicitud.setUsuarioSolicitante(usuario);
        solicitud.setTipoSolicitud(tipoSolicitud);
        solicitud.setEstadoSolicitud(SolicitudAprobacion.EstadoSolicitud.PENDIENTE);

        // Convertir DTOs a Map para JSONB
        try {
            Map<String, Object> propuestos = objectMapper.convertValue(datosCompletos, Map.class);
            solicitud.setDatosPropuestos(propuestos);

            if (datosOriginales != null) {
                Map<String, Object> originales = objectMapper.convertValue(datosOriginales, Map.class);
                solicitud.setDatosOriginales(originales);
            }
        } catch (Exception e) {
            log.error("Error al serializar datos: {}", e.getMessage());
            throw new RuntimeException("Error al procesar datos del emprendimiento");
        }

        // Actualizar estado del emprendimiento
        if (tipoSolicitud == SolicitudAprobacion.TipoSolicitud.CREACION) {
            emprendimiento.setEstadoEmprendimiento(EstadoEmprendimiento.PENDIENTE_APROBACION.name());
            emprendimientosRepository.save(emprendimiento);
        }
        // Si es ACTUALIZACION, el emprendimiento sigue PUBLICADO

        solicitud = solicitudRepository.save(solicitud);

        // Registrar en historial
        registrarHistorial(solicitud, HistorialRevision.AccionRevision.ENVIO,
                "Solicitud enviada para revisión", usuario);

        if (tipoSolicitud == SolicitudAprobacion.TipoSolicitud.CREACION) {
            notificarAdminsNuevaSolicitud(emprendimiento, usuario, solicitud.getId());
        } else {
            notificarAdminsActualizacion(emprendimiento, usuario, solicitud.getId());
        }


        log.info("Solicitud creada exitosamente con ID: {}", solicitud.getId());
        return solicitud;
    }


    @Transactional
    public SolicitudAprobacion guardarPropuesta(
            Integer emprendimientoId,
            EmprendimientoDetallesDTO datosPropuestos,
            Usuarios usuario) {

        log.info("Guardando propuesta para emprendimiento: {}", emprendimientoId);

        Emprendimientos emprendimiento = emprendimientosRepository.findById(emprendimientoId)
                .orElseThrow(() -> new EntityNotFoundException("Emprendimiento no encontrado"));

        // Solo permitir sobre PUBLICADO (actualización)
        if (!EstadoEmprendimiento.PUBLICADO.name().equals(emprendimiento.getEstadoEmprendimiento())) {
            throw new IllegalStateException("Solo se pueden proponer cambios sobre emprendimientos PUBLICADOS");
        }

        // Buscar solicitud activa (PENDIENTE o EN_REVISION, según tu query)
        Optional<SolicitudAprobacion> solicitudActivaOpt =
                solicitudRepository.findSolicitudActivaByEmprendimientoId(emprendimientoId);

        if (solicitudActivaOpt.isEmpty()) {
            // No hay solicitud activa → crear una nueva de ACTUALIZACION
            return crearSolicitud(emprendimientoId, datosPropuestos, usuario);
        }

        SolicitudAprobacion solicitud = solicitudActivaOpt.get();

        // Si está EN_REVISION, usa el flujo existente de modificarYReenviar
        if (solicitud.getEstadoSolicitud() == SolicitudAprobacion.EstadoSolicitud.EN_REVISION) {
            modificarYReenviar(solicitud.getId(), datosPropuestos, usuario);
            // Volvemos a cargar por si quieres devolver actualizado
            return solicitudRepository.findById(solicitud.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Solicitud no encontrada tras modificar"));
        }

        // Si está APROBADO o RECHAZADO, no debería haber sido devuelto como "activa"
        throw new IllegalStateException("No se puede modificar una solicitud en estado " + solicitud.getEstadoSolicitud());
    }


    /**
     * Obtener vista completa para el emprendedor
     */
    public VistaEmprendedorDTO obtenerVistaEmprendedor(Integer emprendimientoId) {
        log.debug("Obteniendo vista emprendedor para emprendimiento: {}", emprendimientoId);

        Emprendimientos emprendimiento = emprendimientosRepository.findById(emprendimientoId)
                .orElseThrow(() -> new EntityNotFoundException("Emprendimiento no encontrado"));

        VistaEmprendedorDTO vista = new VistaEmprendedorDTO();

        // Datos actuales
        EmprendimientoDetallesDTO datosActuales = capturarEstadoCompleto(emprendimientoId);
        vista.setDatosActuales(datosActuales);
        vista.setEstadoEmprendimiento(emprendimiento.getEstadoEmprendimiento());

        // Verificar solicitud activa
        Optional<SolicitudAprobacion> solicitudActiva = solicitudRepository
                .findSolicitudActivaByEmprendimientoId(emprendimientoId);

        if (solicitudActiva.isPresent()) {
            SolicitudAprobacion solicitud = solicitudActiva.get();

            try {
                EmprendimientoDetallesDTO datosPropuestos = objectMapper.convertValue(
                        solicitud.getDatosPropuestos(),
                        EmprendimientoDetallesDTO.class
                );

                vista.setDatosPropuestos(datosPropuestos);
                vista.setEstadoSolicitud(solicitud.getEstadoSolicitud());
                vista.setObservaciones(solicitud.getObservaciones());
                vista.setMotivoRechazo(solicitud.getMotivoRechazo());
                vista.setTieneSolicitudActiva(true);
                vista.setSolicitudId(solicitud.getId());
            } catch (Exception e) {
                log.error("Error al deserializar datos propuestos: {}", e.getMessage());
            }
        } else {
            vista.setTieneSolicitudActiva(false);
        }

        return vista;
    }

    /**
     * Aprobar solicitud y aplicar cambios
     */
    @Transactional
    public void aprobarSolicitud(Integer solicitudId, Usuarios admin) {
        log.info("Aprobando solicitud: {}", solicitudId);

        SolicitudAprobacion solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new EntityNotFoundException("Solicitud no encontrada"));

        Emprendimientos emprendimiento = solicitud.getEmprendimiento();

        try {
            // Deserializar datos propuestos
            EmprendimientoDetallesDTO  datosNuevos = objectMapper.convertValue(
                    solicitud.getDatosPropuestos(),
                    EmprendimientoDetallesDTO.class
            );

            // APLICAR TODOS LOS CAMBIOS
            aplicarCambiosCompletos(emprendimiento, datosNuevos);

            // Actualizar estado
            emprendimiento.setEstadoEmprendimiento(EstadoEmprendimiento.PUBLICADO.name());
            emprendimiento.setEstatusEmprendimiento(true);
            emprendimiento.setFechaActualizacion(LocalDateTime.now());
            emprendimientosRepository.save(emprendimiento);

            // Actualizar solicitud
            solicitud.setEstadoSolicitud(SolicitudAprobacion.EstadoSolicitud.APROBADO);
            solicitud.setFechaRespuesta(LocalDateTime.now());
            solicitud.setUsuarioRevisor(admin);
            solicitudRepository.save(solicitud);

            // Registrar en historial
            registrarHistorial(solicitud, HistorialRevision.AccionRevision.APROBACION,

                    "Emprendimiento aprobado", admin);
// Notificar al emprendedor
            notificacionService.crearNotificacion(
                    emprendimiento.getUsuarios().getId(),
                    "SOLICITUD_APROBADA",
                    Map.of("nombreEmprendimiento", emprendimiento.getNombreComercial()),
                    "/emprendimientos/" + emprendimiento.getId() + "/mi-emprendimiento",
                    emprendimiento.getId(),
                    solicitudId
            );

            log.info("Solicitud aprobada exitosamente");

        } catch (Exception e) {
            log.error("Error al aprobar solicitud: {}", e.getMessage(), e);
            throw new RuntimeException("Error al aplicar cambios: " + e.getMessage());
        }
    }

    /**
     * Rechazar solicitud
     */
    @Transactional
    public void rechazarSolicitud(Integer solicitudId, String motivo, Usuarios admin) {
        log.info("Rechazando solicitud: {}", solicitudId);

        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("Debe proporcionar un motivo de rechazo");
        }

        SolicitudAprobacion solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new EntityNotFoundException("Solicitud no encontrada"));

        solicitud.setEstadoSolicitud(SolicitudAprobacion.EstadoSolicitud.RECHAZADO);
        solicitud.setMotivoRechazo(motivo);
        solicitud.setFechaRespuesta(LocalDateTime.now());
        solicitud.setUsuarioRevisor(admin);
        solicitudRepository.save(solicitud);

        // Si era creación, marcar emprendimiento como rechazado
        if (solicitud.getTipoSolicitud() == SolicitudAprobacion.TipoSolicitud.CREACION) {
            Emprendimientos emp = solicitud.getEmprendimiento();
            emp.setEstadoEmprendimiento(EstadoEmprendimiento.RECHAZADO.name());
            emprendimientosRepository.save(emp);
        }
        // Si era actualización, el emprendimiento sigue PUBLICADO

        // Registrar en historial
        registrarHistorial(solicitud, HistorialRevision.AccionRevision.RECHAZO, motivo, admin);

        notificacionService.crearNotificacion(
                solicitud.getEmprendimiento().getUsuarios().getId(),
                "SOLICITUD_RECHAZADA",
                Map.of(
                        "nombreEmprendimiento", solicitud.getEmprendimiento().getNombreComercial(),
                        "motivo", motivo
                ),
                "/emprendimientos/" + solicitud.getEmprendimiento().getId() + "/mi-emprendimiento",
                solicitud.getEmprendimiento().getId(),
                solicitudId
        );

        log.info("Solicitud rechazada exitosamente");
    }

    /**
     * Enviar observaciones para que el emprendedor modifique
     */
    @Transactional
    public void enviarObservaciones(Integer solicitudId, String observaciones, Usuarios admin) {
        log.info("Enviando observaciones para solicitud: {}", solicitudId);

        if (observaciones == null || observaciones.isBlank()) {
            throw new IllegalArgumentException("Debe proporcionar observaciones");
        }

        SolicitudAprobacion solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new EntityNotFoundException("Solicitud no encontrada"));

        solicitud.setEstadoSolicitud(SolicitudAprobacion.EstadoSolicitud.EN_REVISION);
        solicitud.setObservaciones(observaciones);
        solicitud.setFechaRespuesta(LocalDateTime.now());
        solicitud.setUsuarioRevisor(admin);
        solicitudRepository.save(solicitud);

        // Cambiar estado del emprendimiento
        Emprendimientos emp = solicitud.getEmprendimiento();
        emp.setEstadoEmprendimiento(EstadoEmprendimiento.EN_REVISION.name());
        emprendimientosRepository.save(emp);

        // Registrar en historial
        registrarHistorial(solicitud, HistorialRevision.AccionRevision.OBSERVACIONES,
                observaciones, admin);

        // Notificar al emprendedor
        notificacionService.crearNotificacion(
                emp.getUsuarios().getId(),
                "SOLICITUD_OBSERVACIONES",
                Map.of("nombreEmprendimiento", emp.getNombreComercial()),
                "/solicitudes/" + solicitudId + "/modificar",
                emp.getId(),
                solicitudId
        );

        log.info("Observaciones enviadas exitosamente");
    }

    /**
     * Modificar datos propuestos y reenviar
     */
    @Transactional
    public void modificarYReenviar(Integer solicitudId, EmprendimientoDetallesDTO  datosActualizados, Usuarios usuario) {
        log.info("Modificando y reenviando solicitud: {}", solicitudId);

        SolicitudAprobacion solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new EntityNotFoundException("Solicitud no encontrada"));

        if (solicitud.getEstadoSolicitud() != SolicitudAprobacion.EstadoSolicitud.EN_REVISION) {
            throw new IllegalStateException("La solicitud no está en revisión");
        }

        try {
            // Actualizar datos propuestos en el JSON
            Map<String, Object> propuestos = objectMapper.convertValue(datosActualizados, Map.class);
            solicitud.setDatosPropuestos(propuestos);

            // Cambiar estado a pendiente nuevamente
            solicitud.setEstadoSolicitud(SolicitudAprobacion.EstadoSolicitud.PENDIENTE);
            solicitud.setFechaSolicitud(LocalDateTime.now());
            solicitud.setObservaciones(null); // Limpiar observaciones anteriores
            solicitudRepository.save(solicitud);

            // Actualizar estado del emprendimiento
            Emprendimientos emp = solicitud.getEmprendimiento();
            emp.setEstadoEmprendimiento(EstadoEmprendimiento.PENDIENTE_APROBACION.name());
            emprendimientosRepository.save(emp);

            // Registrar en historial
            registrarHistorial(solicitud, HistorialRevision.AccionRevision.MODIFICACION,
                    "Datos modificados según observaciones", usuario);

            // Notificar a admins
            notificarAdminsModificacion(emp, usuario, solicitudId);

            log.info("Solicitud modificada y reenviada exitosamente");

        } catch (Exception e) {
            log.error("Error al modificar solicitud: {}", e.getMessage(), e);
            throw new RuntimeException("Error al actualizar datos: " + e.getMessage());
        }
    }

    /**
     * Aplicar TODOS los cambios al emprendimiento y sus relaciones
     */
    @Transactional
    protected void  aplicarCambiosCompletos(Emprendimientos emprendimiento, EmprendimientoDetallesDTO  datos) {
        log.debug("Aplicando cambios completos al emprendimiento: {}", emprendimiento.getId());

        Integer empId = emprendimiento.getId();

        // 1. Actualizar datos básicos
        emprendimiento.setNombreComercial(datos.getNombreComercial());
        emprendimiento.setAnioCreacion(datos.getAnioCreacion());
        emprendimiento.setActivoEmprendimiento(datos.getActivoEmprendimiento());
        emprendimiento.setAceptaDatosPublicos(datos.getAceptaDatosPublicos());

        if (datos.getCiudad().getId() != null) {
            Ciudades ciudad = ciudadesRepository.findById(datos.getCiudad().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Ciudad no encontrada"));
            emprendimiento.setCiudades(ciudad);
        }

        if (datos.getTipoEmprendimientoId() != null) {
            TiposEmprendimientos tipo = tiposEmprendimientoRepository.findById(datos.getTipoEmprendimientoId())
                    .orElseThrow(() -> new EntityNotFoundException("Tipo de emprendimiento no encontrado"));
            emprendimiento.setTiposEmprendimientos(tipo);
        }

        emprendimientosRepository.save(emprendimiento);

        // 2. Actualizar/Crear información del representante
        if (datos.getInformacionRepresentante() != null) {
            InformacionRepresentante rep = informacionRepresentanteRepository
                    .findFirstByEmprendimientoId(empId);

            if (rep == null) {
                rep = new InformacionRepresentante();
                rep.setEmprendimiento(emprendimiento);
            }

            InformacionRepresentanteDTO repDTO = datos.getInformacionRepresentante();
            rep.setNombre(repDTO.getNombre());
            rep.setApellido(repDTO.getApellido());
            rep.setCorreoPersonal(repDTO.getCorreoPersonal());
            rep.setCorreoCorporativo(repDTO.getCorreoCorporativo());
            rep.setTelefono(repDTO.getTelefono());
            rep.setIdentificacion(repDTO.getIdentificacion());
            rep.setCarrera(repDTO.getCarrera());
            rep.setSemestre(repDTO.getSemestre());
            rep.setTieneParientesUees(repDTO.getTieneParientesUees());
            rep.setNombrePariente(repDTO.getNombrePariente());
            rep.setAreaPariente(repDTO.getAreaPariente());
            rep.setIntegrantesEquipo(repDTO.getIntegrantesEquipo());
            informacionRepresentanteRepository.save(rep);
        }

        if (datos.getCategorias() != null) {
            emprendimientoCategoriasRepository.deleteEmprendimientoCategoriasByEmprendimientoId(empId);

            datos.getCategorias().forEach(catDTO -> {
                Categorias categoria = categoriasRepository.findById(catDTO.getId())
                        .orElseThrow(() -> new EntityNotFoundException("Categoría no encontrada"));

                EmprendimientoCategorias cat = new EmprendimientoCategorias();

                // Crear y asignar la clave compuesta
                EmprendimientoCategoriasId id = new EmprendimientoCategoriasId(
                                emprendimiento.getId(),
                                categoria.getId()
                        );
                cat.setId(id);

                // Asignar las relaciones
                cat.setEmprendimiento(emprendimiento);
                cat.setCategoria(categoria); // ← Entidad, NO DTO

                emprendimientoCategoriasRepository.save(cat);
            });
        }


        // 4. Actualizar descripciones
        if (datos.getDescripciones() != null) {
            List<DescripcionEmprendimiento> actuales = emprendimientosDescripcionRepository
                    .findByEmprendimientoId(empId);

            // Eliminar las que no están en los nuevos datos
            actuales.forEach(actual -> {
                /*boolean existe = datos.getDescripciones().stream()
                        .anyMatch(d -> d.getTipoDescripcion().equals(actual.getTipoDescripcion()));*/
                DescripcionEmprendimiento empDe = emprendimientosDescripcionRepository.findByEmprendimientoAndDescripciones(emprendimiento, actual.getDescripciones());
                boolean existe = false;
                if(null != empDe){
                    existe = true;
                }
                if (!existe) {
                    emprendimientosDescripcionRepository.delete(actual);
                }
            });

            // Actualizar o crear
            datos.getDescripciones().forEach(descDTO -> {
                Descripciones des = descripcionesRepository.findById(descDTO.getIdDescripcion()).orElse(null);
                DescripcionEmprendimiento desc = actuales.stream()
                        .filter(d ->  d.getEmprendimiento().equals(emprendimiento) &&
                                d.getDescripciones().equals(des)
                        )
                        .findFirst()
                        .orElse(new DescripcionEmprendimiento());

                desc.setDescripciones(des);
                desc.setRespuesta(descDTO.getRespuesta());
                desc.setEmprendimiento(emprendimiento);
                emprendimientosDescripcionRepository.save(desc);
            });
        }

        // 5. Actualizar métricas
        if (datos.getMetricas() != null) {
            List<EmprendimientoMetricas> actuales = emprendimientoMetricaRepository
                    .findByEmprendimientoId(empId);

            // Eliminar las que no están
            actuales.forEach(actual -> {
                boolean existe = datos.getMetricas().stream()
                        .anyMatch(m -> m.getMetricaId().equals(actual.getMetrica().getId()));
                if (!existe) {
                    emprendimientoMetricaRepository.delete(actual);
                }
            });

            // Actualizar o crear
            datos.getMetricas().forEach(metDTO -> {
                EmprendimientoMetricas metrica = actuales.stream()
                        .filter(m -> m.getMetrica().getId().equals(metDTO.getMetricaId()))
                        .findFirst()
                        .orElse(null);

                if (metrica != null) {
                    metrica.setValor(metDTO.getValor());
                    emprendimientoMetricaRepository.save(metrica);
                } else {
                    MetricasBasicas tipoMetrica = tiposMetricasRepository.findById(metDTO.getMetricaId())
                            .orElseThrow(() -> new EntityNotFoundException("Métrica no encontrada"));

                    EmprendimientoMetricas nuevaMetrica = new EmprendimientoMetricas();
                    nuevaMetrica.setMetrica(tipoMetrica);
                    nuevaMetrica.setEmprendimiento(emprendimiento);
                    nuevaMetrica.setValor(metDTO.getValor());
                    emprendimientoMetricaRepository.save(nuevaMetrica);
                }
            });
        }

        // 6. Actualizar presencia digital
        if (datos.getPresenciasDigitales() != null) {
            List<TiposPresenciaDigital> actuales = emprendimientoPresenciaDigitalRepository
                    .findByEmprendimientoId(empId);

            // Eliminar las que no están
            actuales.forEach(actual -> {
                boolean existe = datos.getPresenciasDigitales().stream()
                        .anyMatch(p -> p.getPlataforma().equals(actual.getPlataforma()));
                if (!existe) {
                    emprendimientoPresenciaDigitalRepository.delete(actual);
                }
            });

            // Actualizar o crear
            datos.getPresenciasDigitales().forEach(presDTO -> {
                TiposPresenciaDigital presencia = actuales.stream()
                        .filter(p -> p.getPlataforma().equals(presDTO.getPlataforma()))
                        .findFirst()
                        .orElse(new TiposPresenciaDigital());

                presencia.setPlataforma(presDTO.getPlataforma());
                presencia.setDescripcion(presDTO.getDescripcion());
                presencia.setEmprendimiento(emprendimiento);
                emprendimientoPresenciaDigitalRepository.save(presencia);
            });
        }

        // 7. Actualizar declaraciones
        if (datos.getDeclaracionesFinales() != null) {
            List<EmprendimientoDeclaraciones> actuales = emprendimientoDeclaracionesRepository
                    .findByEmprendimientoId(empId);

            // Eliminar las que no están
            actuales.forEach(actual -> {
                boolean existe = datos.getDeclaracionesFinales().stream()
                        .anyMatch(d -> d.getDeclaracionId().equals(actual.getDeclaracion().getId()));
                if (!existe) {
                    emprendimientoDeclaracionesRepository.delete(actual);
                }
            });

            // Actualizar o crear
            datos.getDeclaracionesFinales().forEach(declDTO -> {
                EmprendimientoDeclaraciones declaracion = actuales.stream()
                        .filter(d -> d.getDeclaracion().getId().equals(declDTO.getDeclaracionId()))
                        .findFirst()
                        .orElse(null);

                if (declaracion != null) {
                    declaracion.setAceptada(declDTO.getAceptada());
                    declaracion.setNombreFirma(declDTO.getNombreFirma());
                    declaracion.setFechaAceptacion(declDTO.getFechaAceptacion());
                    emprendimientoDeclaracionesRepository.save(declaracion);
                } else {
                    DeclaracionesFinales tipoDeclaracion = declaracionesFinalesRepository
                            .findById(declDTO.getDeclaracionId())
                            .orElseThrow(() -> new EntityNotFoundException("Declaración no encontrada"));

                    EmprendimientoDeclaraciones nuevaDecl = new EmprendimientoDeclaraciones();
                    nuevaDecl.setDeclaracion(tipoDeclaracion);
                    nuevaDecl.setEmprendimiento(emprendimiento);
                    nuevaDecl.setAceptada(declDTO.getAceptada());
                    nuevaDecl.setNombreFirma(declDTO.getNombreFirma());
                    nuevaDecl.setFechaAceptacion(declDTO.getFechaAceptacion());
                    emprendimientoDeclaracionesRepository.save(nuevaDecl);
                }
            });
        }

        // 8. Actualizar participación
        if (datos.getParticipacionesComunidad() != null) {
            List<EmprendimientoParticipacion> actuales = emprendimientoParticicipacionComunidadRepository
                    .findByEmprendimientoId(empId);

            // Eliminar las que no están
            actuales.forEach(actual -> {
                boolean existe = datos.getParticipacionesComunidad().stream()
                        .anyMatch(p -> p.getOpcionParticipacionId().equals(actual.getOpcionParticipacion().getId()));
                if (!existe) {
                    emprendimientoParticicipacionComunidadRepository.delete(actual);
                }
            });

            // Actualizar o crear
            datos.getParticipacionesComunidad().forEach(partDTO -> {
                EmprendimientoParticipacion participacion = actuales.stream()
                        .filter(p -> p.getOpcionParticipacion().getId().equals(partDTO.getOpcionParticipacionId()))
                        .findFirst()
                        .orElse(null);

                if (participacion != null) {
                    participacion.setRespuesta(partDTO.getRespuesta());
                    emprendimientoParticicipacionComunidadRepository.save(participacion);
                } else {
                    OpcionesParticipacionComunidad opcion = opcionesParticipacionComunidadRepository
                            .findById(partDTO.getOpcionParticipacionId())
                            .orElseThrow(() -> new EntityNotFoundException("Opción de participación no encontrada"));

                    EmprendimientoParticipacion nuevaPart = new EmprendimientoParticipacion();
                    nuevaPart.setOpcionParticipacion(opcion);
                    nuevaPart.setEmprendimiento(emprendimiento);
                    nuevaPart.setRespuesta(partDTO.getRespuesta());
                    emprendimientoParticicipacionComunidadRepository.save(nuevaPart);
                }
            });
        }

        log.debug("Cambios aplicados exitosamente");
    }

    /**
     * Listar solicitudes pendientes para el admin
     */
    public PageResponseDTO<SolicitudAprobacionListadoDTO> listarPendientes(Pageable pageable) {
        Page<SolicitudAprobacion> page = solicitudRepository
                .findByEstadoSolicitudOrderByFechaSolicitudAsc(
                        SolicitudAprobacion.EstadoSolicitud.PENDIENTE,
                        pageable
                );

        Page<SolicitudAprobacionListadoDTO> dtoPage = page.map(this::mapToListadoDTO);

        return PageResponseDTO.fromPage(dtoPage);
    }


    /**
     * Obtener detalle de solicitud con comparación
     */
    public Map<String, Object> obtenerDetalleConComparacion(Integer solicitudId) {
        SolicitudAprobacion solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new EntityNotFoundException("Solicitud no encontrada"));

        Map<String, Object> detalle = new HashMap<>();
        detalle.put("solicitud", mapSolicitudToDTO(solicitud));

        // ===== PROpuestos =====
        Map<String, Object> propuestosMap = new HashMap<>(solicitud.getDatosPropuestos());
        propuestosMap.remove("usuario");

        EmprendimientoDetallesDTO propuestosDto =
                objectMapper.convertValue(propuestosMap, EmprendimientoDetallesDTO.class);


        detalle.put("datosPropuestos", propuestosDto);

        // ===== ORIginAles (solo ACTUALIZACION) =====
        if (solicitud.getDatosOriginales() != null) {
            Map<String, Object> originalesMap = new HashMap<>(solicitud.getDatosOriginales());
            originalesMap.remove("usuario");

            EmprendimientoDetallesDTO originalesDto =
                    objectMapper.convertValue(originalesMap, EmprendimientoDetallesDTO.class);

            detalle.put("datosOriginales", originalesDto);

            detalle.put("diferencias", calcularDiferencias(
                    solicitud.getDatosOriginales(),
                    solicitud.getDatosPropuestos()
            ));
        }

        return detalle;
    }


    /**
     * Obtener historial de una solicitud
     */
    public List<Map<String, Object>> obtenerHistorial(Integer solicitudId) {
        List<HistorialRevision> historial = historialRepository
                .findBySolicitudIdOrderByFechaAccionDesc(solicitudId);

        return historial.stream()
                .map(h -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", h.getId());
                    item.put("accion", h.getAccion());
                    item.put("comentario", h.getComentario());
                    item.put("fechaAccion", h.getFechaAccion());
                    item.put("usuario", h.getUsuario().getNombre() + " " + h.getUsuario().getApellido());
                    return item;
                })
                .collect(Collectors.toList());
    }

    // ===== MÉTODOS AUXILIARES =====

    private void registrarHistorial(SolicitudAprobacion solicitud,
                                    HistorialRevision.AccionRevision accion,
                                    String comentario,
                                    Usuarios usuario) {
        HistorialRevision historial = new HistorialRevision();
        historial.setSolicitud(solicitud);
        historial.setAccion(accion);
        historial.setComentario(comentario);
        historial.setUsuario(usuario);
        historialRepository.save(historial);
    }

    private Map<String, Object> calcularDiferencias(
            Map<String, Object> originales,
            Map<String, Object> propuestos) {

        Map<String, Object> diferencias = new HashMap<>();

        propuestos.forEach((key, valorNuevo) -> {
            Object valorOriginal = originales.get(key);

            if (valorOriginal == null && valorNuevo != null) {
                diferencias.put(key, Map.of(
                        "tipo", "AGREGADO",
                        "nuevo", valorNuevo
                ));
            } else if (valorOriginal != null && !valorOriginal.equals(valorNuevo)) {
                diferencias.put(key, Map.of(
                        "tipo", "MODIFICADO",
                        "original", valorOriginal,
                        "nuevo", valorNuevo
                ));
            }
        });

        return diferencias;
    }

    /**
     * Notificar a todos los administradores sobre nueva solicitud
     */
    private void notificarAdminsNuevaSolicitud(Emprendimientos emprendimiento, Usuarios usuario, Integer solicitudId) {
        try {
            List<Usuarios> admins = userRepository.findAllByRol_Nombre(("ADMIN"));

            for (Usuarios admin : admins) {
                notificacionService.crearNotificacion(
                        admin.getId(),
                        "NUEVA_SOLICITUD",
                        Map.of(
                                "nombreUsuario", usuario.getNombre() + " " + usuario.getApellido(),
                                "nombreEmprendimiento", emprendimiento.getNombreComercial()
                        ),
                        "/admin/solicitudes/" + solicitudId,
                        emprendimiento.getId(),
                        solicitudId
                );
            }

            log.info("Notificaciones enviadas a {} administradores", admins.size());
        } catch (Exception e) {
            log.error("Error al enviar notificaciones a admins: {}", e.getMessage());
            // No lanzar excepción para no afectar el flujo principal
        }
    }

    /**
     * Notificar a administradores sobre solicitud de actualización
     */
    private void notificarAdminsActualizacion(Emprendimientos emprendimiento, Usuarios usuario, Integer solicitudId) {
        try {
            List<Usuarios> admins = userRepository.findAllByRol_Nombre(("ADMIN"));

            for (Usuarios admin : admins) {
                notificacionService.crearNotificacion(
                        admin.getId(),
                        "ACTUALIZACION_SOLICITADA",
                        Map.of("nombreEmprendimiento", emprendimiento.getNombreComercial()),
                        "/admin/solicitudes/" + solicitudId,
                        emprendimiento.getId(),
                        solicitudId
                );
            }

            log.info("Notificaciones de actualización enviadas a {} administradores", admins.size());
        } catch (Exception e) {
            log.error("Error al enviar notificaciones de actualización: {}", e.getMessage());
        }
    }

    /**
     * Notificar a administradores sobre solicitud modificada
     */
    private void notificarAdminsModificacion(Emprendimientos emprendimiento, Usuarios usuario, Integer solicitudId) {
        try {
            List<Usuarios> admins = userRepository.findAllByRol_Nombre(("ADMIN"));

            for (Usuarios admin : admins) {
                notificacionService.crearNotificacion(
                        admin.getId(),
                        "SOLICITUD_MODIFICADA",
                        Map.of(
                                "nombreUsuario", usuario.getNombre() + " " + usuario.getApellido(),
                                "nombreEmprendimiento", emprendimiento.getNombreComercial()
                        ),
                        "/admin/solicitudes/" + solicitudId,
                        emprendimiento.getId(),
                        solicitudId
                );
            }

            log.info("Notificaciones de modificación enviadas a {} administradores", admins.size());
        } catch (Exception e) {
            log.error("Error al enviar notificaciones de modificación: {}", e.getMessage());
        }
    }

    // Métodos de mapeo DTO


    private SolicitudAprobacionListadoDTO mapToListadoDTO(SolicitudAprobacion s) {
        Emprendimientos e = s.getEmprendimiento();

        return SolicitudAprobacionListadoDTO.builder()
                .id(s.getId())
                .emprendimientoId(e.getId())
                .nombreEmprendimiento(e.getNombreComercial())
                .tipoSolicitud(s.getTipoSolicitud().name())
                .estadoSolicitud(s.getEstadoSolicitud().name())

                .ciudadId(e.getCiudades().getId())
                .nombreCiudad(e.getCiudades().getNombreCiudad())

                .tipoEmprendimiento(e.getTiposEmprendimientos().getSubTipo())
                .anioCreacion(e.getAnioCreacion())

                .fechaSolicitud(s.getFechaSolicitud())
                .nombreSolicitante(s.getUsuarioSolicitante().getNombre())
                .build();
    }



    private InformacionRepresentanteDTO mapRepresentanteToDTO(InformacionRepresentante rep) {
        return InformacionRepresentanteDTO.builder()
                .nombre(rep.getNombre())
                .apellido(rep.getApellido())
                .correoPersonal(rep.getCorreoPersonal())
                .correoCorporativo(rep.getCorreoCorporativo())
                .telefono(rep.getTelefono())
                .identificacion(rep.getIdentificacion())
                .carrera(rep.getCarrera())
                .semestre(rep.getSemestre())
                .tieneParientesUees(rep.getTieneParientesUees())
                .nombrePariente(rep.getNombrePariente())
                .areaPariente(rep.getAreaPariente())
                .integrantesEquipo(rep.getIntegrantesEquipo())
                .build();
    }

    private EmprendimientoCategoriaDTO mapCategoriaToDTO(EmprendimientoCategorias cat) {
        EmprendimientoCategoriaDTO dto = new EmprendimientoCategoriaDTO();

        // Mapear emprendimiento a DTO
        if (cat.getEmprendimiento() != null) {
            EmprendimientoDTO empDTO = new EmprendimientoDTO();
            empDTO.setId(cat.getEmprendimiento().getId());
            empDTO.setNombreComercialEmprendimiento(cat.getEmprendimiento().getNombreComercial());
            empDTO.setFechaCreacion(cat.getEmprendimiento().getFechaCreacion());
            empDTO.setEstadoEmpredimiento(cat.getEmprendimiento().getEstatusEmprendimiento());
            empDTO.setDatosPublicos(cat.getEmprendimiento().getAceptaDatosPublicos());

            // Mapear ciudad si existe
            if (cat.getEmprendimiento().getCiudades() != null) {
                empDTO.setCiudad(cat.getEmprendimiento().getCiudades().getId());
                empDTO.setProvinia(cat.getEmprendimiento().getCiudades().getProvincias().getId());
            }

            // Mapear tipo de emprendimiento si existe
            if (cat.getEmprendimiento().getTiposEmprendimientos() != null) {
                empDTO.setTipoEmprendimientoId(cat.getEmprendimiento().getTiposEmprendimientos().getId());
                empDTO.setTipoEmprendimiento(cat.getEmprendimiento().getTiposEmprendimientos().getSubTipo());
            }

            dto.setEmprendimiento(empDTO);
        }

        // Mapear categoría a DTO
        if (cat.getCategoria() != null) {
            CategoriasDTO catDTO = new CategoriasDTO();
            catDTO.setId(cat.getCategoria().getId());
            catDTO.setNombre(cat.getCategoria().getNombre());
            catDTO.setDescripcion(cat.getCategoria().getDescripcion());
            catDTO.setUrlImagen(cat.getCategoria().getMultimedia().getUrlArchivo());
            catDTO.setIdMultimedia(cat.getCategoria().getMultimedia().getId());

            dto.setCategoria(catDTO);
            dto.setNombreCategoria(cat.getCategoria().getNombre());
        }

        return dto;
    }

    private EmprendimientoDescripcionDTO mapDescripcionToDTO(DescripcionEmprendimiento desc) {
        EmprendimientoDescripcionDTO dto = new EmprendimientoDescripcionDTO();
        dto.setIdDescripcion(desc.getDescripciones().getId());
        dto.setRespuesta(desc.getRespuesta());
        dto.setIdEmprendimiento(
                desc.getEmprendimiento() != null ? desc.getEmprendimiento().getId() : null
        );
        dto.setDescripcionBase(
                desc.getDescripciones() != null ? desc.getDescripciones().getDescripcion() : null
        );
        return dto;
    }

    private EmprendimientoMetricasDTO mapMetricaToDTO(EmprendimientoMetricas metrica) {
        EmprendimientoMetricasDTO dto = new EmprendimientoMetricasDTO();
        dto.setEmprendimientoId(metrica.getEmprendimiento().getId());
        dto.setMetricaId(metrica.getMetrica().getId());
        dto.setValor(metrica.getValor());
        return dto;
    }

    private EmprendimientoPresenciaDigitalDTO mapPresenciaDigitalToDTO(TiposPresenciaDigital pd) {
        EmprendimientoPresenciaDigitalDTO dto = new EmprendimientoPresenciaDigitalDTO();
        dto.setPlataforma(pd.getPlataforma());
        dto.setDescripcion(pd.getDescripcion());
        return dto;
    }

    private EmprendimientoDeclaracionesDTO mapDeclaracionToDTO(EmprendimientoDeclaraciones decl) {
        EmprendimientoDeclaracionesDTO dto = new EmprendimientoDeclaracionesDTO();
        dto.setEmprendimientoId(decl.getEmprendimiento().getId());
        dto.setDeclaracionId(decl.getDeclaracion().getId());
        dto.setAceptada(decl.getAceptada());
        dto.setNombreFirma(decl.getNombreFirma());
        dto.setFechaAceptacion(decl.getFechaAceptacion());
        return dto;
    }

    private EmprendimientoParticipacionDTO mapParticipacionToDTO(EmprendimientoParticipacion part) {
        EmprendimientoParticipacionDTO dto = new EmprendimientoParticipacionDTO();
        dto.setEmprendimientoId(part.getEmprendimiento().getId());
        dto.setOpcionParticipacionId(part.getOpcionParticipacion().getId());
        dto.setRespuesta(part.getRespuesta());
        dto.setNombreOpcionParticipacion(
                part.getOpcionParticipacion() != null ? part.getOpcionParticipacion().getOpcion() : null
        );
        return dto;
    }


    private SolicitudAprobacionDTO mapSolicitudToDTO(SolicitudAprobacion solicitud) {
        return SolicitudAprobacionDTO.builder()
                .id(solicitud.getId())
                .emprendimientoId(solicitud.getEmprendimiento().getId())
                .nombreEmprendimiento(solicitud.getEmprendimiento().getNombreComercial())
                .tipoSolicitud(solicitud.getTipoSolicitud())
                .estadoSolicitud(solicitud.getEstadoSolicitud())
                .observaciones(solicitud.getObservaciones())
                .motivoRechazo(solicitud.getMotivoRechazo())
                .fechaSolicitud(solicitud.getFechaSolicitud())
                .fechaRespuesta(solicitud.getFechaRespuesta())
                .nombreSolicitante(solicitud.getUsuarioSolicitante().getNombre() + " " +
                        solicitud.getUsuarioSolicitante().getApellido())
                .nombreRevisor(solicitud.getUsuarioRevisor() != null ?
                        solicitud.getUsuarioRevisor().getNombre() + " " +
                                solicitud.getUsuarioRevisor().getApellido() : null)
                .build();
    }
}