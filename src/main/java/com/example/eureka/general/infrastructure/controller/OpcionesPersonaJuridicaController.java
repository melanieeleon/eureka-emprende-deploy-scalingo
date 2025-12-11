package com.example.eureka.general.infrastructure.controller;

import com.example.eureka.general.infrastructure.dto.OpcionesPersonaJuridicaDTO;
import com.example.eureka.general.port.in.OpcionesPersonaJuridicaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/opciones-persona-juridica")
public class OpcionesPersonaJuridicaController {

    private final OpcionesPersonaJuridicaService opcionesPersonaJuridicaService;

    @GetMapping
    public ResponseEntity<?> listarOpcionesPersonaJuridica() {
        return ResponseEntity.ok(opcionesPersonaJuridicaService.listarOpcionesPersonaJuridica());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerOpcionPersonaJuridicaPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(opcionesPersonaJuridicaService.obtenerOpcionPersonaJuridicaPorId(id));
    }

    @PostMapping("/crear")
    public ResponseEntity<?> crearOpcionPersonaJuridica(@RequestBody OpcionesPersonaJuridicaDTO dto) {
        return ResponseEntity.ok(opcionesPersonaJuridicaService.crearOpcionPersonaJuridica(dto));
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<?> actualizarOpcionPersonaJuridica(@PathVariable Integer id, @RequestBody OpcionesPersonaJuridicaDTO dto) {
        return ResponseEntity.ok(opcionesPersonaJuridicaService.actualizarOpcionPersonaJuridica(id, dto));
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminarOpcionPersonaJuridica(@PathVariable Integer id) {
        opcionesPersonaJuridicaService.eliminarOpcionPersonaJuridica(id);
        return ResponseEntity.ok().build();
    }

}
