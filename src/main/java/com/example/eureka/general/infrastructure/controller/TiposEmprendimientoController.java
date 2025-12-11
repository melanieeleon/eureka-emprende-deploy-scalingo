package com.example.eureka.general.infrastructure.controller;

import com.example.eureka.general.infrastructure.dto.TipoEmprendimientoDTO;
import com.example.eureka.general.port.in.TiposEmprendimientoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/tipos-emprendimiento")
@RequiredArgsConstructor
public class TiposEmprendimientoController {

    private final TiposEmprendimientoService service;

    @GetMapping
    public List<TipoEmprendimientoDTO> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public TipoEmprendimientoDTO obtenerPorId(@PathVariable Integer id) {
        return service.obtenerPorId(id);
    }

    @PostMapping("/crear")
    public TipoEmprendimientoDTO guardar(@RequestBody TipoEmprendimientoDTO dto) {
        return service.guardar(dto);
    }

    @PutMapping("/actualizar/{id}")
    public TipoEmprendimientoDTO actualizar(@PathVariable Integer id, @RequestBody TipoEmprendimientoDTO dto) {
        return service.actualizar(id, dto);
    }

    @DeleteMapping("/eliminar/{id}")
    public void eliminar(@PathVariable Integer id) {
        service.eliminar(id);
    }
}
