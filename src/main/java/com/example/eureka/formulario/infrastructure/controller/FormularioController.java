package com.example.eureka.formulario.infrastructure.controller;

import com.example.eureka.autoevaluacion.port.in.AutoevaluacionService;
import com.example.eureka.entrepreneurship.domain.model.OpcionRespuesta;
import com.example.eureka.formulario.domain.model.Opciones;
import com.example.eureka.autoevaluacion.domain.model.Respuesta;
import com.example.eureka.formulario.infrastructure.dto.response.FormularioResponseDTO;
import com.example.eureka.formulario.infrastructure.dto.response.OpcionRespuestaDTO;
import com.example.eureka.formulario.infrastructure.dto.response.OpcionRespuestaResponseDTO;
import com.example.eureka.formulario.port.in.FormularioService;
import com.example.eureka.formulario.port.in.OpcionRespuestaService;
import com.example.eureka.formulario.port.in.OpcionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/formularios")
public class FormularioController {

    private final FormularioService formularioService;
    private final OpcionRespuestaService opcionRespuestaService;
    private final AutoevaluacionService respuestaService;
    private final OpcionService opcionService;


    public FormularioController(FormularioService formularioService, OpcionRespuestaService opcionRespuestaService, AutoevaluacionService respuestaService, OpcionService opcionService) {
        this.formularioService = formularioService;
        this.opcionRespuestaService = opcionRespuestaService;
        this.respuestaService = respuestaService;
        this.opcionService = opcionService;
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<FormularioResponseDTO> getFormularioByTipo(@PathVariable String tipo) {
        FormularioResponseDTO formulario = formularioService.getFormularioByTipo(tipo);
        return ResponseEntity.ok(formulario);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FormularioResponseDTO> getFormularioById(@PathVariable Long id) {
        FormularioResponseDTO formulario = formularioService.getFormularioById(id);
        return ResponseEntity.ok(formulario);
    }

    @PostMapping("/save-opcion-respuesta")
    public ResponseEntity<OpcionRespuestaDTO> save(@RequestBody OpcionRespuestaResponseDTO opcionRespuestaDTO) {

        Respuesta respuesta = respuestaService.findById(opcionRespuestaDTO.getIdRespuesta().longValue());
        Opciones opcion = opcionService.findById(opcionRespuestaDTO.getIdOpcion().longValue());
        OpcionRespuesta respuestaDTO = new OpcionRespuesta();
        respuestaDTO.setRespuesta(respuesta);
        respuestaDTO.setOpciones(opcion);
        respuestaDTO.setValorescala(opcionRespuestaDTO.getValorescala());
        return ResponseEntity.ok(opcionRespuestaService.save(respuestaDTO));
    }

    @GetMapping("/get-respuesta/{idRespuesta}")
    public ResponseEntity<Page<OpcionRespuestaDTO>> findAllByRespuesta(@PathVariable Long idRespuesta, @org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        Respuesta respuesta = respuestaService.findById(idRespuesta);
        return ResponseEntity.ok(opcionRespuestaService.findAllByRespuesta(respuesta, pageable));
    }



}