package com.example.eureka.general.infrastructure.controller;

import com.example.eureka.general.infrastructure.dto.RolesDTO;
import com.example.eureka.general.port.out.IRolesRepository;
import com.example.eureka.auth.domain.Roles;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/roles")
@RequiredArgsConstructor
public class RolesController {

    private final IRolesRepository rolesRepository;

    @GetMapping()
    public ResponseEntity<?> obtenerRoles() {

        List<Roles> roles = rolesRepository.findAll();

        List<RolesDTO> categoriasDTOList = roles.stream().map(rol -> {
            RolesDTO dto = new RolesDTO();
            dto.setNombre(rol.getNombre());
            return dto;
        }).toList();

        return ResponseEntity.ok(categoriasDTOList);
    }
}
