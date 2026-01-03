package com.example.eureka.formulario.infrastructure.controller;

import com.example.eureka.autoevaluacion.infrastructure.dto.RespuestaResponseDTO;
import com.example.eureka.autoevaluacion.port.in.AutoevaluacionService;
import com.example.eureka.autoevaluacion.domain.model.Respuesta;
import com.example.eureka.formulario.infrastructure.dto.response.FormularioResponseDTO;
import com.example.eureka.formulario.infrastructure.dto.response.OpcionRespuestaDTO;
import com.example.eureka.formulario.infrastructure.dto.response.OpcionRespuestaRequestDTO;
import com.example.eureka.formulario.port.in.FormularioService;
import com.example.eureka.formulario.port.in.OpcionRespuestaService;
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


    public FormularioController(FormularioService formularioService, OpcionRespuestaService opcionRespuestaService) {
        this.formularioService = formularioService;
        this.opcionRespuestaService = opcionRespuestaService;
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<FormularioResponseDTO> getFormularioByTipo(@PathVariable String tipo) {
        FormularioResponseDTO formulario = formularioService.getFormularioByTipo(tipo);
        return ResponseEntity.ok(formulario);
    }


    @PostMapping("/save-opcion-respuesta")
    public ResponseEntity<List<OpcionRespuestaDTO>> save( @RequestBody List<OpcionRespuestaRequestDTO> opcionRespuestaDTO) {
        return ResponseEntity.ok(opcionRespuestaService.save(opcionRespuestaDTO));
    }


}