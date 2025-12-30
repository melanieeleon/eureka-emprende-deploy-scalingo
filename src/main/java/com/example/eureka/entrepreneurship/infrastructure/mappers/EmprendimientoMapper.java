package com.example.eureka.entrepreneurship.infrastructure.mappers;

import com.example.eureka.auth.domain.Usuarios;
import com.example.eureka.entrepreneurship.domain.model.*;
import com.example.eureka.entrepreneurship.infrastructure.dto.response.EmprendimientoListadoResponseDTO;
import com.example.eureka.entrepreneurship.infrastructure.dto.response.EmprendimientoPublicoDTO;
import com.example.eureka.entrepreneurship.infrastructure.dto.shared.*;
import com.example.eureka.general.domain.model.Categorias;
import com.example.eureka.general.domain.model.Ciudades;
import com.example.eureka.general.domain.model.TiposEmprendimientos;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class EmprendimientoMapper {

    public static EmprendimientoResponseDTO toResponseDTO(Emprendimientos emprendimiento) {
        if (emprendimiento == null) return null;

        EmprendimientoResponseDTO dto = new EmprendimientoResponseDTO();

        dto.setId(emprendimiento.getId());
        dto.setNombreComercial(emprendimiento.getNombreComercial());
        dto.setAnioCreacion(emprendimiento.getAnioCreacion());
        dto.setActivoEmprendimiento(emprendimiento.getActivoEmprendimiento());
        dto.setAceptaDatosPublicos(emprendimiento.getAceptaDatosPublicos());
        dto.setFechaCreacion(emprendimiento.getFechaCreacion());
        dto.setFechaActualizacion(emprendimiento.getFechaActualizacion());
        dto.setEstadoEmprendimiento(emprendimiento.getEstadoEmprendimiento());

        // --- Relaciones ---
        if (emprendimiento.getUsuarios() != null) {
            dto.setUsuarioId(emprendimiento.getUsuarios().getId());
            dto.setNombreUsuario(emprendimiento.getUsuarios().getNombre());
        }

        if (emprendimiento.getCiudades() != null) {
            dto.setCiudadId(emprendimiento.getCiudades().getId());
            dto.setNombreCiudad(emprendimiento.getCiudades().getNombreCiudad());
        }

        if (emprendimiento.getTiposEmprendimientos() != null) {
            dto.setTipoEmprendimientoId(emprendimiento.getTiposEmprendimientos().getId());
            dto.setNombreTipoEmprendimiento(emprendimiento.getTiposEmprendimientos().getSubTipo());
        }

        // ← AGREGAR ESTE MAPEO DE MULTIMEDIA
        if (emprendimiento.getMultimedia() != null && !emprendimiento.getMultimedia().isEmpty()) {
            dto.setMultimedia(toMultimediaDTOList(emprendimiento.getMultimedia()));
        }

        return dto;
    }


    public static Emprendimientos toDTOResponse(EmprendimientoResponseDTO emprendimiento) {
        if (emprendimiento == null) return null;

        Emprendimientos dto = new Emprendimientos();

        dto.setId(emprendimiento.getId());
        dto.setNombreComercial(emprendimiento.getNombreComercial());
        dto.setAnioCreacion(emprendimiento.getAnioCreacion());
        dto.setActivoEmprendimiento(emprendimiento.getActivoEmprendimiento());
        dto.setAceptaDatosPublicos(emprendimiento.getAceptaDatosPublicos());
        dto.setFechaCreacion(emprendimiento.getFechaCreacion());
        dto.setFechaActualizacion(emprendimiento.getFechaActualizacion());
        dto.setEstadoEmprendimiento(emprendimiento.getEstadoEmprendimiento());

        // --- Relaciones ---
        if(emprendimiento.getUsuarioId() != null){
            Usuarios user = new Usuarios();
            user.setId(emprendimiento.getUsuarioId());
            user.setNombre(emprendimiento.getNombreUsuario());
            dto.setUsuarios(user);
        }

        if(emprendimiento.getCiudadId() != null){
            Ciudades ciudad = new Ciudades();
            ciudad.setId(emprendimiento.getCiudadId());
            ciudad.setNombreCiudad(emprendimiento.getNombreCiudad());
            dto.setCiudades(ciudad);
        }

        if(emprendimiento.getTipoEmprendimientoId() != null){
            TiposEmprendimientos tipoEmprendimiento = new TiposEmprendimientos();
            tipoEmprendimiento.setId(emprendimiento.getTipoEmprendimientoId());
            tipoEmprendimiento.setSubTipo(emprendimiento.getNombreTipoEmprendimiento());
            dto.setTiposEmprendimientos(tipoEmprendimiento);
        }

        return dto;
    }

    public static List<EmprendimientoResponseDTO> toResponseList(List<Emprendimientos> emprendimientos) {
        return emprendimientos.stream()
                .map(EmprendimientoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public static List<CategoriaDTO> toCategoriaDTOList(List<EmprendimientoCategorias> categorias) {
        return categorias.stream()
                .map(cat -> {
                    if (cat.getCategoria() != null) {
                        CategoriaDTO dto = new CategoriaDTO();
                        dto.setId(cat.getCategoria().getId());
                        dto.setNombre(cat.getCategoria().getNombre());
                        dto.setDescripcion(cat.getCategoria().getDescripcion());

                        // Validar que multimedia existe antes de acceder
                        if (cat.getCategoria().getMultimedia() != null) {
                            dto.setMultimediaId(cat.getCategoria().getMultimedia().getId());
                        }

                        return dto;
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static List<EmprendimientoDescripcionDTO> toDescripcionDTOList(List<DescripcionEmprendimiento> descripciones) {
        return descripciones.stream().map(desc -> {
            EmprendimientoDescripcionDTO dto = new EmprendimientoDescripcionDTO();
            dto.setIdEmprendimiento(desc.getEmprendimiento().getId());
            dto.setIdDescripcion(desc.getDescripciones().getId());
            dto.setRespuesta(desc.getRespuesta());
            dto.setDescripcionBase(desc.getDescripciones().getDescripcion());
            return dto;
        }).collect(Collectors.toList());
    }

    public static List<EmprendimientoPresenciaDigitalDTO> toPresenciaDigitalDTOList(List<TiposPresenciaDigital> presencias) {
        return presencias.stream().map(p -> {
            EmprendimientoPresenciaDigitalDTO dto = new EmprendimientoPresenciaDigitalDTO();
            dto.setPlataforma(p.getPlataforma());
            dto.setDescripcion(p.getDescripcion());
            return dto;
        }).collect(Collectors.toList());
    }

    public static List<EmprendimientoMetricasDTO> toMetricasDTOList(List<EmprendimientoMetricas> metricas) {
        return metricas.stream().map(m -> {
            EmprendimientoMetricasDTO dto = new EmprendimientoMetricasDTO();
            dto.setMetricaId(m.getMetrica().getId());
            dto.setValor(m.getValor());
            dto.setEmprendimientoId(m.getEmprendimiento().getId());
            return dto;
        }).collect(Collectors.toList());
    }

    public static List<EmprendimientoDeclaracionesDTO> toDeclaracionesDTOList(List<EmprendimientoDeclaraciones> declaraciones) {
        return declaraciones.stream().map(d -> {
            EmprendimientoDeclaracionesDTO dto = new EmprendimientoDeclaracionesDTO();
            dto.setDeclaracionId(d.getDeclaracion().getId());
            dto.setAceptada(d.getAceptada());
            dto.setNombreFirma(d.getNombreFirma());
            dto.setFechaAceptacion(d.getFechaAceptacion());
            dto.setEmprendimientoId(d.getEmprendimiento().getId());
            return dto;
        }).collect(Collectors.toList());
    }

    public static List<EmprendimientoParticipacionDTO> toParticipacionDTOList(List<EmprendimientoParticipacion> participaciones) {
        return participaciones.stream().map(p -> {
            EmprendimientoParticipacionDTO dto = new EmprendimientoParticipacionDTO();
            dto.setOpcionParticipacionId(p.getOpcionParticipacion().getId());
            dto.setRespuesta(p.getRespuesta());
            dto.setEmprendimientoId(p.getEmprendimiento().getId());
            if (p.getOpcionParticipacion() != null) {
                dto.setEmprendimientoId(p.getEmprendimiento().getId());
                dto.setNombreOpcionParticipacion(p.getOpcionParticipacion().getOpcion());
            }
            return dto;
        }).collect(Collectors.toList());
    }

    public static InformacionRepresentanteDTO toRepresentanteDTO(InformacionRepresentante info) {
        if (info == null) return null;
        InformacionRepresentanteDTO dto = new InformacionRepresentanteDTO();
        dto.setNombre(info.getNombre());
        dto.setApellido(info.getApellido());
        dto.setCorreoPersonal(info.getCorreoPersonal());
        dto.setCorreoCorporativo(info.getCorreoCorporativo());
        dto.setIdentificacion(info.getIdentificacion());
        dto.setCarrera(info.getCarrera());
        dto.setAreaPariente(info.getAreaPariente());
        dto.setSemestre(info.getSemestre());
        dto.setFechaGraduacion(info.getFechaGraduacion());
        dto.setTieneParientesUees(info.getTieneParientesUees());
        dto.setNombrePariente(info.getNombrePariente());

        return dto;
    }

    public static CategoriaDTO toCategoriaDTO(Categorias categoria) {
        if (categoria == null) return null;
        CategoriaDTO dto = new CategoriaDTO();
        dto.setId(categoria.getId());
        dto.setNombre(categoria.getNombre());
        dto.setDescripcion(categoria.getDescripcion());
        if (categoria.getMultimedia() != null) {
            dto.setMultimediaId(categoria.getMultimedia().getId());
        }
        return dto;
    }

    // En EmprendimientoMapper.java
    public static List<MultimediaDTO> toMultimediaDTOList(List<EmprendimientoMultimedia> multimediaList) {
        if (multimediaList == null) {
            return null;
        }
        return multimediaList.stream()
                .map(EmprendimientoMapper::toMultimediaDTO)
                .collect(Collectors.toList());
    }

    public static MultimediaDTO toMultimediaDTO(EmprendimientoMultimedia empMultimedia) {
        if (empMultimedia == null || empMultimedia.getMultimedia() == null) {
            return null;
        }

        Multimedia multimedia = empMultimedia.getMultimedia();

        return MultimediaDTO.builder()
                .id(multimedia.getId())
                .urlArchivo(multimedia.getUrlArchivo())
                .nombreActivo(multimedia.getNombreActivo())
                .tipo(empMultimedia.getTipo())
                .build();
    }


    public static EmprendimientoPublicoDTO toPublicoDTO(Emprendimientos e) {
        EmprendimientoPublicoDTO dto = new EmprendimientoPublicoDTO();
        dto.setId(e.getId());
        dto.setNombreComercial(e.getNombreComercial());
        dto.setAnioCreacion(e.getAnioCreacion());
        dto.setActivoEmprendimiento(e.getActivoEmprendimiento());
        dto.setAceptaDatosPublicos(e.getAceptaDatosPublicos());
        dto.setFechaCreacion(e.getFechaCreacion());
        dto.setFechaActualizacion(e.getFechaActualizacion());
        dto.setEstadoEmprendimiento(e.getEstadoEmprendimiento());

        if (e.getUsuarios() != null) {
            dto.setUsuarioId(e.getUsuarios().getId().intValue());
            dto.setNombreUsuario(e.getUsuarios().getNombre());
        }
        if (e.getCiudades() != null) {
            dto.setCiudadId(e.getCiudades().getId().intValue());
            dto.setNombreCiudad(e.getCiudades().getNombreCiudad());
        }
        if (e.getTiposEmprendimientos() != null) {
            dto.setTipoEmprendimientoId(e.getTiposEmprendimientos().getId().intValue());
            dto.setNombreTipoEmprendimiento(e.getTiposEmprendimientos().getSubTipo());
        }

        return dto;
    }

    public static EmprendimientoDescripcionDTO mapDescripcionToDTO(DescripcionEmprendimiento desc) {
        EmprendimientoDescripcionDTO dto = new EmprendimientoDescripcionDTO();
        dto.setIdEmprendimiento(desc.getEmprendimiento().getId().intValue());
        dto.setIdDescripcion(desc.getDescripciones().getId().intValue());
        dto.setDescripcionBase(desc.getDescripciones().getDescripcion());
        dto.setRespuesta(desc.getRespuesta());
        return dto;
    }

    // 3) Presencia digital -> DTO
    public static EmprendimientoPresenciaDigitalDTO toPresenciaDigitalDTO(TiposPresenciaDigital entidad) {
        EmprendimientoPresenciaDigitalDTO dto = new EmprendimientoPresenciaDigitalDTO();
        dto.setPlataforma(entidad.getPlataforma());
        dto.setDescripcion(entidad.getDescripcion());
        return dto;
    }

}
