package com.example.eureka.formulario.infrastructure.controller;

import com.example.eureka.autoevaluacion.infrastructure.dto.RespuestaResponseDTO;
import com.example.eureka.autoevaluacion.port.in.AutoevaluacionService;
import com.example.eureka.entrepreneurship.domain.model.OpcionRespuesta;
import com.example.eureka.entrepreneurship.port.in.EmprendimientoService;
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

import java.util.List;

@RestController
@RequestMapping("/v1/formularios")
public class FormularioController {

    private final FormularioService formularioService;
    private final OpcionRespuestaService opcionRespuestaService;
    private final AutoevaluacionService respuestaService;


    public FormularioController(FormularioService formularioService, OpcionRespuestaService opcionRespuestaService, AutoevaluacionService respuestaService) {
        this.formularioService = formularioService;
        this.opcionRespuestaService = opcionRespuestaService;
        this.respuestaService = respuestaService;
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

    @GetMapping("/emprendimiento/{idEmprendimiento}")
    public ResponseEntity<List<FormularioResponseDTO>> getFormularioByIdEmprendimiento(@PathVariable Integer idEmprendimiento){
        return ResponseEntity.ok(formularioService.getFormularioByEmprendimiento(idEmprendimiento));
    }

    @PostMapping("/save-opcion-respuesta/{idUsuario}")
    public ResponseEntity<List<OpcionRespuestaDTO>> save(@PathVariable Integer idUsuario, @RequestBody List<OpcionRespuestaResponseDTO> opcionRespuestaDTO) {
        return ResponseEntity.ok(opcionRespuestaService.save(opcionRespuestaDTO, idUsuario));
    }

    @PostMapping("/autoevaluacion/save")
    public ResponseEntity<RespuestaResponseDTO> saveAutoevaluacion(@RequestBody RespuestaResponseDTO respuestaDTO) {
        return ResponseEntity.ok(opcionRespuestaService.generaRespuestaAutoevaluacion(respuestaDTO));
    }

    @GetMapping("/get-respuesta/{idRespuesta}")
    public ResponseEntity<Page<OpcionRespuestaDTO>> findAllByRespuesta(@PathVariable Long idRespuesta, @org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        Respuesta respuesta = respuestaService.findById(idRespuesta);
        return ResponseEntity.ok(opcionRespuestaService.findAllByRespuesta(respuesta, pageable));
    }



}