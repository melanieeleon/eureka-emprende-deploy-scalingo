package com.example.eureka.general.infrastructure.controller;

import com.example.eureka.general.infrastructure.dto.CiudadDTO;
import com.example.eureka.general.port.in.CiudadService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/ciudad")
@RequiredArgsConstructor
public class CiudadController {

    private final CiudadService ciudadService;

    @GetMapping
    public List<CiudadDTO> listar() {
        return ciudadService.listar();
    }

    @GetMapping("/{id}")
    public CiudadDTO obtenerPorId(@PathVariable Integer id) {
        return ciudadService.obtenerPorId(id);
    }

    @GetMapping("/provincia/{id}")
    public List<CiudadDTO> obtenerCiudadPorProvinciaId(@PathVariable Integer id) {
        return ciudadService.obtenerCiudadPorProvinciaId(id);
    }

    @PostMapping("/crear")
    public CiudadDTO guardar(@RequestBody CiudadDTO dto) {
        return ciudadService.guardar(dto);
    }

    @PutMapping("/actualizar/{id}")
    public CiudadDTO actualizar(@PathVariable Integer id, @RequestBody CiudadDTO dto) {
        return ciudadService.actualizar(id, dto);
    }

    @DeleteMapping("/eliminar/{id}")
    public void eliminar(@PathVariable Integer id) {
        ciudadService.eliminar(id);
    }
}
