package com.example.eureka.formulario.application.service;

import com.example.eureka.autoevaluacion.infrastructure.dto.RespuestaResponseDTO;
import com.example.eureka.autoevaluacion.port.out.IAutoevaluacionRepository;
import com.example.eureka.entrepreneurship.domain.model.Emprendimientos;
import com.example.eureka.entrepreneurship.domain.model.OpcionRespuesta;
import com.example.eureka.entrepreneurship.port.out.IEmprendimientosRepository;
import com.example.eureka.entrepreneurship.port.out.IPreguntaRepository;
import com.example.eureka.formulario.domain.model.Formulario;
import com.example.eureka.formulario.domain.model.Opciones;
import com.example.eureka.autoevaluacion.domain.model.Respuesta;
import com.example.eureka.formulario.domain.model.Pregunta;
import com.example.eureka.formulario.infrastructure.dto.response.OpcionResponseDTO;
import com.example.eureka.formulario.infrastructure.dto.response.OpcionRespuestaDTO;
import com.example.eureka.formulario.infrastructure.dto.response.OpcionRespuestaResponseDTO;
import com.example.eureka.formulario.port.in.OpcionRespuestaService;
import com.example.eureka.formulario.port.out.IFormularioRepository;
import com.example.eureka.formulario.port.out.IOpcionRepository;
import com.example.eureka.formulario.port.out.IOpcionRespuestaRepository;
import com.example.eureka.notificacion.port.in.NotificacionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class OpcionRespuestaServiceImpl implements OpcionRespuestaService {

    private final IOpcionRespuestaRepository opcionRespuestaRepository;
    private final IEmprendimientosRepository  emprendimientosRepository;
    private final IOpcionRepository  opcionRepository;
    private final IAutoevaluacionRepository   autoevaluacionRepository;
    private final IFormularioRepository formularioRepository;
    private final IPreguntaRepository preguntaRepository;
    private final NotificacionService  notificacionService;

    public OpcionRespuestaServiceImpl(IOpcionRespuestaRepository opcionRespuestaRepository, IEmprendimientosRepository emprendimientosRepository, IOpcionRepository opcionRepository, IAutoevaluacionRepository autoevaluacionRepository, IFormularioRepository formularioRepository,  IPreguntaRepository preguntaRepository, NotificacionService notificacionService) {
        this.opcionRespuestaRepository = opcionRespuestaRepository;
        this.emprendimientosRepository = emprendimientosRepository;
        this.opcionRepository = opcionRepository;
        this.autoevaluacionRepository = autoevaluacionRepository;
        this.formularioRepository = formularioRepository;
        this.preguntaRepository = preguntaRepository;
        this.notificacionService = notificacionService;
    }

    @Override
    public Page<OpcionRespuestaDTO> findAllByRespuesta(Respuesta respuesta, Pageable pageable) {
        List<OpcionRespuestaDTO> opcionRespuestaDTOs = new ArrayList<>();
        List<OpcionRespuesta> opcionRespuestas = opcionRespuestaRepository.findAllByRespuesta(respuesta);
        if(opcionRespuestas != null) {


            for(OpcionRespuesta opcionRespuesta : opcionRespuestas) {

              /*  OpcionRespuestaDTO opcionRespuestaDTO = new OpcionRespuestaDTO();
                opcionRespuestaDTO.setId(opcionRespuesta.getId());
                opcionRespuestaDTO.setOpciones(opcionRespuesta.getOpciones());
                opcionRespuestaDTO.setRespuesta(opcionRespuesta.getRespuesta());
                opcionRespuestaDTO.setValorescala(opcionRespuesta.getValorescala());
                opcionRespuestaDTO.setEmprendimientos(opcionRespuesta.getEmprendimiento());
                opcionRespuestaDTOs.add(opcionRespuestaDTO);*/

                // Buscar si ya existe un DTO con el mismo ID
                OpcionRespuestaDTO existente = opcionRespuestaDTOs.stream()
                        .filter(dto -> dto.getId().equals(opcionRespuesta.getId()))
                        .findFirst()
                        .orElse(null);

                if (existente != null) {
                    // Ya existe → solo agregamos la opción a la lista
                    if (existente.getOpciones() == null) {
                        existente.setOpciones(new ArrayList<>());
                    }
                    OpcionResponseDTO op = new OpcionResponseDTO();
                    op.setIdOpcion(opcionRespuesta.getOpciones().getIdOpcion());
                    op.setOpcion(opcionRespuesta.getOpciones().getOpcion());
                    existente.getOpciones().add(op);
                    continue; // No crear un nuevo DTO
                }

                // No existe → crear uno nuevo
                OpcionRespuestaDTO nuevo = new OpcionRespuestaDTO();
                nuevo.setId(opcionRespuesta.getId());

                // Crear lista de opciones
                List<OpcionResponseDTO> listaOpciones = new ArrayList<>();
                OpcionResponseDTO op = new OpcionResponseDTO();
                op.setIdOpcion(opcionRespuesta.getOpciones().getIdOpcion());
                op.setOpcion(opcionRespuesta.getOpciones().getOpcion());
                listaOpciones.add(op);
                nuevo.setOpciones(listaOpciones);

                nuevo.setIdRespuesta(opcionRespuesta.getRespuesta().getId());
                nuevo.setValorescala(opcionRespuesta.getValorescala());
                nuevo.setIdEmprendimientos(opcionRespuesta.getEmprendimiento() == null ? 0 : opcionRespuesta.getEmprendimiento().getId());
                nuevo.setIdPregunta(opcionRespuesta.getPregunta() == null ? 0 :  opcionRespuesta.getPregunta().getIdPregunta().intValue());

                opcionRespuestaDTOs.add(nuevo);


            }


            return  new PageImpl<>(opcionRespuestaDTOs, pageable, opcionRespuestaDTOs.size());
        }
        return new PageImpl<>(List.of(), pageable, opcionRespuestaDTOs.size());
    }

    @Override
    public Page<OpcionRespuestaDTO> findAllByOpciones(Opciones opciones, Pageable pageable) {
        List<OpcionRespuestaDTO> opcionRespuestaDTOs = new ArrayList<>();
        List<OpcionRespuesta> opcionRespuestas = opcionRespuestaRepository.findAllByOpciones(opciones);
        if(opcionRespuestas != null) {
            for(OpcionRespuesta opcionRespuesta : opcionRespuestas) {
                /*OpcionRespuestaDTO opcionRespuestaDTO = new OpcionRespuestaDTO();
                opcionRespuestaDTO.setId(opcionRespuesta.getId());
                opcionRespuestaDTO.setOpciones(opcionRespuesta.getOpciones());
                opcionRespuestaDTO.setRespuesta(opcionRespuesta.getRespuesta());
                opcionRespuestaDTO.setValorescala(opcionRespuesta.getValorescala());
                opcionRespuestaDTO.setEmprendimientos(opcionRespuesta.getEmprendimiento());
                opcionRespuestaDTOs.add(opcionRespuestaDTO);*/

                // Buscar si ya existe un DTO con el mismo ID
                OpcionRespuestaDTO existente = opcionRespuestaDTOs.stream()
                        .filter(dto -> dto.getId().equals(opcionRespuesta.getId()))
                        .findFirst()
                        .orElse(null);

                if (existente != null) {
                    // Ya existe → solo agregamos la opción a la lista
                    if (existente.getOpciones() == null) {
                        existente.setOpciones(new ArrayList<>());
                    }
                    OpcionResponseDTO op = new OpcionResponseDTO();
                    op.setIdOpcion(opcionRespuesta.getOpciones().getIdOpcion());
                    op.setOpcion(opcionRespuesta.getOpciones().getOpcion());
                    existente.getOpciones().add(op);
                    continue; // No crear un nuevo DTO
                }

                // No existe → crear uno nuevo
                OpcionRespuestaDTO nuevo = new OpcionRespuestaDTO();
                nuevo.setId(opcionRespuesta.getId());

                // Crear lista de opciones
                List<OpcionResponseDTO> listaOpciones = new ArrayList<>();

                OpcionResponseDTO op = new OpcionResponseDTO();
                op.setIdOpcion(opcionRespuesta.getOpciones().getIdOpcion());
                op.setOpcion(opcionRespuesta.getOpciones().getOpcion());
                listaOpciones.add(op);
                nuevo.setOpciones(listaOpciones);

                nuevo.setIdRespuesta(opcionRespuesta.getRespuesta().getId());
                nuevo.setValorescala(opcionRespuesta.getValorescala());
                nuevo.setIdEmprendimientos(opcionRespuesta.getEmprendimiento() == null ? 0 : opcionRespuesta.getEmprendimiento().getId());
                nuevo.setIdPregunta(opcionRespuesta.getPregunta() == null ? 0 : opcionRespuesta.getPregunta().getIdPregunta().intValue());

                opcionRespuestaDTOs.add(nuevo);

            }
            return  new PageImpl<>(opcionRespuestaDTOs, pageable, opcionRespuestaDTOs.size());

        }
        return new PageImpl<>(List.of(), pageable, opcionRespuestaDTOs.size());
    }

    @Override
    public List<OpcionRespuestaDTO> save(List<OpcionRespuestaResponseDTO> ls, Integer idUsuario) {
        List<OpcionRespuestaDTO> opcionRespuestaDTOs = new ArrayList<>();
        double sumaValores = 0;
        int contador = 0;
        Emprendimientos emp = new Emprendimientos();
        for(OpcionRespuestaResponseDTO opcionRespuesta : ls){
            emp = emprendimientosRepository.findById(opcionRespuesta.getIdEmprendimiento()).orElse(null);
            Pregunta p = preguntaRepository.findById(opcionRespuesta.getIdsPregunta().longValue()).orElse(null);
            Respuesta rp = autoevaluacionRepository.findById(opcionRespuesta.getIdRespuesta()).orElse(null);
            List<OpcionResponseDTO> idsOpciones = new ArrayList<>();
            OpcionRespuesta op = new  OpcionRespuesta();
            for(Integer id: opcionRespuesta.getIdsOpciones()){
                op = new OpcionRespuesta();
                Opciones opc = opcionRepository.findById(id.longValue());
                op.setRespuesta(rp);
                op.setOpciones(opc);
                op.setEmprendimiento(emp);
                op.setPregunta(p);
                op.setValorescala(opcionRespuesta.getValorescala());
                op = opcionRespuestaRepository.save(op);
                OpcionResponseDTO opr = new OpcionResponseDTO();
                opr.setIdOpcion(op.getOpciones().getIdOpcion());
                opr.setOpcion(op.getOpciones().getOpcion());
                idsOpciones.add(opr);
            }


            OpcionRespuestaDTO  opcionRespuestaDTO = new OpcionRespuestaDTO();
            opcionRespuestaDTO.setId(op.getId());
            opcionRespuestaDTO.setOpciones(idsOpciones);
            opcionRespuestaDTO.setIdRespuesta(op.getRespuesta().getId());
            opcionRespuestaDTO.setIdPregunta(p.getIdPregunta().intValue());
            opcionRespuestaDTO.setIdEmprendimientos(op.getEmprendimiento().getId());
            opcionRespuestaDTO.setValorescala(op.getValorescala());

            opcionRespuestaDTOs.add(opcionRespuestaDTO);

            if (opcionRespuesta.getValorescala() != null) {
                sumaValores += opcionRespuesta.getValorescala();
                contador++;
            }
        }

        double promedio = contador > 0 ? sumaValores / contador : 0;

        System.out.println("Promedio de valorescala: " + promedio);

        if(promedio <= 2){
            String mensaje = "El emprendimiento "+ emp.getNombreComercial() +" se debe de generar la autoevaluación la valoración obtenida es: " + promedio;
            notificacionService.crearNotificacionAutoevaluacion(idUsuario, "AUTOEVALUACION_REQUERIDA", "Autoevaluación requerida", mensaje, "", emp.getId());
        }


        return opcionRespuestaDTOs;
    }

    @Override
    public RespuestaResponseDTO generaRespuestaAutoevaluacion(RespuestaResponseDTO respuesta){
        Formulario fm = formularioRepository.findByTipoFormularioNombre("AUTOEVALUACION").orElse(null);
        Respuesta r = autoevaluacionRepository.findById(respuesta.getIdRespuesta()).orElse(null);
        Respuesta autovaloracion = new Respuesta();
        autovaloracion.setRespuesta(r);
        autovaloracion.setEmprendimientos(r.getEmprendimientos());
        autovaloracion.setFormulario(fm);
        autovaloracion.setEsAutoEvaluacion(true);
        autovaloracion.setFechaRespuesta(LocalDateTime.now());
        Respuesta response = autoevaluacionRepository.save(autovaloracion);

        RespuestaResponseDTO respuestaDTO = new RespuestaResponseDTO();
        respuestaDTO.setId(response.getId());
        respuestaDTO.setIdRespuesta(response.getRespuesta().getId());
        respuestaDTO.setEsAutoevaluacion(response.getEsAutoEvaluacion());
        respuestaDTO.setFechaRespuesta(response.getFechaRespuesta());
        respuestaDTO.setIdFormulario(response.getFormulario().getIdFormulario().intValue());
        return respuestaDTO;

    }
}
