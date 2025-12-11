package com.example.eureka.entrepreneurship.infrastructure.controller;

import com.example.eureka.entrepreneurship.infrastructure.dto.shared.TipoDescripcionEmprendimientoDTO;
import com.example.eureka.entrepreneurship.port.in.TipoDescripcionEmprendimientoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/tipos-descripcion-emprendimiento")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPRENDEDOR')")
public class TiposDescripcionEmprendimientoController {

    private final TipoDescripcionEmprendimientoService service;

    @GetMapping
    public List<TipoDescripcionEmprendimientoDTO> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public TipoDescripcionEmprendimientoDTO obtenerPorId(@PathVariable Integer id) {
        return service.obtenerPorId(id);
    }

    @PostMapping("/crear")
    public TipoDescripcionEmprendimientoDTO guardar(@RequestBody TipoDescripcionEmprendimientoDTO dto) {
        return service.guardar(dto);
    }

    @PutMapping("/actualizar/{id}")
    public TipoDescripcionEmprendimientoDTO actualizar(@PathVariable Integer id, @RequestBody TipoDescripcionEmprendimientoDTO dto) {
        return service.actualizar(id, dto);
    }

    @DeleteMapping("/eliminar/{id}")
    public void eliminar(@PathVariable Integer id) {
        service.eliminar(id);
    }
}
