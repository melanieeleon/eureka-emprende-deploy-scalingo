package com.example.eureka.general.infrastructure.controller;

import com.example.eureka.general.infrastructure.dto.ProvinciaDTO;
import com.example.eureka.general.port.in.ProvinciaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/provincia")
@RequiredArgsConstructor
public class ProvinciaController {

    private final ProvinciaService provinciaService;

    @GetMapping
    public ResponseEntity<List<ProvinciaDTO>> obtenerProvincias() {
        List<ProvinciaDTO> provincias = provinciaService.obtenerProvincias();
        return ResponseEntity.ok(provincias);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProvinciaDTO> obtenerProvinciaPorId(@PathVariable Integer id) {
        ProvinciaDTO provincia = provinciaService.obtenerProvinciaPorId(id);
        return ResponseEntity.ok(provincia);
    }

    @PostMapping("/crear")
    public ResponseEntity<Integer> crearProvincia(@RequestBody ProvinciaDTO provinciaDTO) {
        Integer id = provinciaService.crearProvincia(provinciaDTO);
        return ResponseEntity.ok(id);
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<Integer> actualizarProvincia(@PathVariable Integer id, @RequestBody ProvinciaDTO provinciaDTO) {
        Integer idProvincia = provinciaService.actualizarProvincia(id, provinciaDTO);
        return ResponseEntity.ok(idProvincia);
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminarProvincia(@PathVariable Integer id) {
        provinciaService.eliminarProvincia(id);
        return ResponseEntity.ok().build();
    }
}
