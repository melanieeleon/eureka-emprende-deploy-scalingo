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
import com.example.eureka.formulario.infrastructure.dto.response.OpcionRespuestaRequestDTO;
import com.example.eureka.formulario.port.in.OpcionRespuestaService;
import com.example.eureka.formulario.port.out.IFormularioRepository;
import com.example.eureka.formulario.port.out.IOpcionRepository;
import com.example.eureka.formulario.port.out.IOpcionRespuestaRepository;
import com.example.eureka.metricas.port.in.MetricasPreguntaService;
import com.example.eureka.notificacion.port.in.NotificacionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    private final MetricasPreguntaService metricasPreguntaService;


    public OpcionRespuestaServiceImpl(IOpcionRespuestaRepository opcionRespuestaRepository, IEmprendimientosRepository emprendimientosRepository, IOpcionRepository opcionRepository, IAutoevaluacionRepository autoevaluacionRepository, IFormularioRepository formularioRepository,  IPreguntaRepository preguntaRepository, NotificacionService notificacionService,  MetricasPreguntaService metricasPreguntaService) {
        this.opcionRespuestaRepository = opcionRespuestaRepository;
        this.emprendimientosRepository = emprendimientosRepository;
        this.opcionRepository = opcionRepository;
        this.autoevaluacionRepository = autoevaluacionRepository;
        this.formularioRepository = formularioRepository;
        this.preguntaRepository = preguntaRepository;
        this.notificacionService = notificacionService;
        this.metricasPreguntaService = metricasPreguntaService;   // <--

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
                nuevo.setPregunta(
                        opcionRespuesta.getPregunta() == null ? null : opcionRespuesta.getPregunta().getPregunta()
                );
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
    @Transactional
    public List<OpcionRespuestaDTO> save(List<OpcionRespuestaRequestDTO> ls) {
        List<OpcionRespuestaDTO> opcionRespuestaDTOs = new ArrayList<>();
        List<OpcionRespuesta> respuestasGuardadas = new ArrayList<>();   // NUEVO: acumular respuestas
        double sumaValores = 0;
        int contador = 0;

        Emprendimientos emp = null;
        Respuesta cabeceraValoracion = null;
        Respuesta cabeceraAutoevaluacion = null;

        for (OpcionRespuestaRequestDTO opcionRespuesta : ls) {

            // Emprendimiento (solo se carga una vez)
            if (emp == null) {
                emp = emprendimientosRepository.findById(opcionRespuesta.getIdEmprendimiento())
                        .orElse(null);
            }

            Pregunta p = preguntaRepository.findById(opcionRespuesta.getIdsPregunta().longValue())
                    .orElse(null);

            // 1) Determinar la cabecera Respuesta a usar (rp)
            Respuesta rp;

            if (opcionRespuesta.getIdRespuesta() != null) {
                // CASO: Ya existe cabecera (valoración o autoevaluación)
                rp = autoevaluacionRepository.findById(opcionRespuesta.getIdRespuesta())
                        .orElse(null);
            } else {
                // No hay cabecera aún: decidir si es VALORACIÓN o AUTOEVALUACIÓN
                if (opcionRespuesta.getIdRespuestaValoracion() == null) {
                    // CASO: VALORACIÓN (primer guardado)
                    if (cabeceraValoracion == null) {
                        String tipo = opcionRespuesta.getTipoFormulario();

                        Formulario fm = formularioRepository
                                .findByTipoFormularioNombre(tipo)
                                .orElse(null);

                        cabeceraValoracion = new Respuesta();
                        cabeceraValoracion.setEmprendimientos(emp);
                        cabeceraValoracion.setFormulario(fm);
                        cabeceraValoracion.setEsAutoEvaluacion(false);
                        cabeceraValoracion.setFechaRespuesta(LocalDateTime.now());

                        cabeceraValoracion = autoevaluacionRepository.save(cabeceraValoracion);
                    }
                    rp = cabeceraValoracion;
                } else {
                    // CASO: AUTOEVALUACIÓN (primer guardado)
                    if (cabeceraAutoevaluacion == null) {
                        // Cargar la valoración origen
                        Respuesta valoracionOrigen = autoevaluacionRepository
                                .findById(opcionRespuesta.getIdRespuestaValoracion())
                                .orElse(null);

                        Formulario fmAuto = formularioRepository
                                .findByTipoFormularioNombre("AUTOEVALUACION")
                                .orElse(null);

                        cabeceraAutoevaluacion = new Respuesta();
                        cabeceraAutoevaluacion.setEmprendimientos(emp);
                        cabeceraAutoevaluacion.setFormulario(fmAuto);
                        cabeceraAutoevaluacion.setEsAutoEvaluacion(true);
                        cabeceraAutoevaluacion.setFechaRespuesta(LocalDateTime.now());
                        cabeceraAutoevaluacion.setRespuesta(valoracionOrigen);  // relación con la valoración origen

                        cabeceraAutoevaluacion = autoevaluacionRepository.save(cabeceraAutoevaluacion);
                    }
                    rp = cabeceraAutoevaluacion;
                }
            }

            List<OpcionResponseDTO> idsOpciones = new ArrayList<>();
            OpcionRespuesta op = null;

            // 2) Guardar opciones (para MULTIPLE / OPCION_UNICA)
            if (opcionRespuesta.getIdsOpciones() != null && !opcionRespuesta.getIdsOpciones().isEmpty()) {
                for (Integer id : opcionRespuesta.getIdsOpciones()) {
                    op = new OpcionRespuesta();
                    Opciones opc = opcionRepository.findById(id.longValue());
                    op.setRespuesta(rp);
                    op.setOpciones(opc);
                    op.setEmprendimiento(emp);
                    op.setPregunta(p);
                    op.setValorescala(opcionRespuesta.getValorescala());
                    op = opcionRespuestaRepository.save(op);
                    respuestasGuardadas.add(op);   // NUEVO

                    OpcionResponseDTO opr = new OpcionResponseDTO();
                    opr.setIdOpcion(op.getOpciones().getIdOpcion());
                    opr.setOpcion(op.getOpciones().getOpcion());
                    idsOpciones.add(opr);
                }
            } else {
                // 3) CASO ESCALA sin opciones: guardar registro solo con valorescala
                op = new OpcionRespuesta();
                op.setRespuesta(rp);
                op.setOpciones(null);
                op.setEmprendimiento(emp);
                op.setPregunta(p);
                op.setValorescala(opcionRespuesta.getValorescala());
                op = opcionRespuestaRepository.save(op);
                respuestasGuardadas.add(op);       // NUEVO
            }

            // 4) Construir DTO de salida
            OpcionRespuestaDTO opcionRespuestaDTO = new OpcionRespuestaDTO();
            opcionRespuestaDTO.setId(op.getId());
            opcionRespuestaDTO.setOpciones(idsOpciones);
            opcionRespuestaDTO.setIdRespuesta(rp.getId());
            opcionRespuestaDTO.setIdPregunta(p.getIdPregunta().intValue());
            opcionRespuestaDTO.setIdEmprendimientos(emp.getId());
            opcionRespuestaDTO.setValorescala(op.getValorescala());

            opcionRespuestaDTOs.add(opcionRespuestaDTO);

            // 5) Acumular para promedio (solo si hay escala) - promedio general
            if (opcionRespuesta.getValorescala() != null) {
                sumaValores += opcionRespuesta.getValorescala();
                contador++;
            }
        }

        double promedio = contador > 0 ? sumaValores / contador : 0;

        System.out.println("Promedio de valorescala: " + promedio);

        // NUEVO: procesar métricas por pregunta con las respuestas guardadas
        if (emp != null && !respuestasGuardadas.isEmpty()) {
            procesarValoracion(emp, respuestasGuardadas);
        }

        // 6) Si el promedio es bajo, crear notificación de AUTOEVALUACION_REQUERIDA
        if (promedio <= 2 && emp != null && cabeceraValoracion != null) {
            Integer idEmprendedor = emp.getUsuarios().getId();
            Integer idValoracion = cabeceraValoracion.getId();
            String mensaje = "El emprendimiento " + emp.getNombreComercial()
                    + " se debe de generar la autoevaluación la valoración obtenida es: " + promedio;
            notificacionService.crearNotificacionAutoevaluacion(
                    idEmprendedor,
                    "AUTOEVALUACION_REQUERIDA",
                    "Autoevaluación requerida",
                    mensaje,
                    idValoracion != null ? idValoracion.toString() : "",
                    emp.getId()
            );
        }

        return opcionRespuestaDTOs;
    }




    /*
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
*/

    private void procesarValoracion(Emprendimientos emp, List<OpcionRespuesta> respuestasGuardadas) {

        Map<Pregunta, List<OpcionRespuesta>> porPregunta = respuestasGuardadas.stream()
                .filter(r -> r.getPregunta() != null)
                .collect(Collectors.groupingBy(OpcionRespuesta::getPregunta));

        for (Map.Entry<Pregunta, List<OpcionRespuesta>> entry : porPregunta.entrySet()) {
            Pregunta pregunta = entry.getKey();
            List<OpcionRespuesta> respuestasPregunta = entry.getValue();

            double suma = respuestasPregunta.stream()
                    .filter(r -> r.getValorescala() != null)
                    .mapToInt(OpcionRespuesta::getValorescala)
                    .sum();

            long total = respuestasPregunta.stream()
                    .filter(r -> r.getValorescala() != null)
                    .count();

            double promedioPregunta = total > 0 ? suma / total : 0.0;

            metricasPreguntaService.guardarOActualizar(emp, pregunta, promedioPregunta, total);
        }
    }

}
