package com.example.eureka.autoevaluacion.infrastructure.controller;

import com.example.eureka.autoevaluacion.infrastructure.dto.RespuestaResponseDTO;
import com.example.eureka.entrepreneurship.domain.model.Emprendimientos;
import com.example.eureka.autoevaluacion.infrastructure.dto.EmprendimientoInfo;
import com.example.eureka.autoevaluacion.infrastructure.dto.RespuestaFormularioDTO;
import com.example.eureka.autoevaluacion.port.in.AutoevaluacionService;
import com.example.eureka.autoevaluacion.domain.model.Respuesta;
import com.example.eureka.entrepreneurship.infrastructure.dto.shared.EmprendimientoResponseDTO;
import com.example.eureka.entrepreneurship.infrastructure.mappers.EmprendimientoMapper;
import com.example.eureka.entrepreneurship.port.in.EmprendimientoService;
import com.example.eureka.formulario.port.in.FormularioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/autoevaluacion")
@RequiredArgsConstructor
public class AutoevaluacionController {

    private final AutoevaluacionService autoevaluacionService;
    private final EmprendimientoService emprendimientoService;
    private final FormularioService  formularioService;


    @GetMapping("/emprendimientos")
    public ResponseEntity<Page<EmprendimientoInfo>> obtenerEmprendimientos(@org.springdoc.core.annotations.ParameterObject Pageable pageable){
        return ResponseEntity.ok(autoevaluacionService.obtenerEmprendimientos(pageable));
    }

    @GetMapping("/respuesta-emprendimiento/{id}")
    public ResponseEntity<HashMap<String, Object>>  obtenerRespuestaEmprendimiento(@PathVariable Integer id){
        EmprendimientoResponseDTO emprendimientos = emprendimientoService.obtenerEmprendimientoPorId(id);
        Emprendimientos emprendimientos1 = EmprendimientoMapper.toDTOResponse(emprendimientos);
        boolean valida = autoevaluacionService.existsByEmprendimientos(emprendimientos1);
        HashMap<String, Object> respuesta = new HashMap<>();
        if(valida){
            List<RespuestaFormularioDTO> res = autoevaluacionService.obtenerRespuestasPorEmprendimiento(emprendimientos.getId().longValue());
            respuesta.put("status", 200);
            respuesta.put("message", res);
            return ResponseEntity.ok(respuesta);
        }else{
            respuesta.put("status", 203);
            respuesta.put("message","No hay respuestas del emprendimiento");
            return ResponseEntity.ok(respuesta);
        }

    }

    @PostMapping("/save")
    public ResponseEntity<Respuesta> saveRespuesta(@RequestBody RespuestaResponseDTO respuesta){
        return ResponseEntity.ok(autoevaluacionService.saveRespuesta(respuesta));
    }

}
