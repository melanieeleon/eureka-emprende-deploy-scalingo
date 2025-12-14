package com.example.eureka.auth.infrastructure.controller;

import com.example.eureka.auth.infrastructure.dto.UsuarioPerfilDTO;
import com.example.eureka.auth.port.in.UsuariosService;
import com.example.eureka.auth.domain.Usuarios;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/v1/usuarios")
@RequiredArgsConstructor
public class UsuariosController {

    private final UsuariosService usuariosServiceImpl;

    @PostMapping
    public Usuarios createUsuario(@RequestBody Usuarios usuario) {
        return usuariosServiceImpl.crearUsuario(usuario);
    }

    @GetMapping("/perfil")
    public ResponseEntity<UsuarioPerfilDTO> obtenerPerfilUsuario(Authentication authentication) {
        // Validar que la autenticación no sea null
        if (authentication == null || authentication.getName() == null) {
            throw new RuntimeException("Usuario no autenticado");
        }

        String email = authentication.getName();
        UsuarioPerfilDTO perfil = usuariosServiceImpl.obtenerUsuarioPorEmail(email);
        return ResponseEntity.ok(perfil);
    }

    @PutMapping("/{id}")
    public Usuarios updateUsuario(@PathVariable Integer id, @RequestBody Usuarios usuario, Authentication authentication) {
        // Validar autenticación
        if (authentication == null || authentication.getName() == null) {
            throw new RuntimeException("Usuario no autenticado");
        }

        // Obtener el usuario autenticado
        String emailAutenticado = authentication.getName();
        Usuarios usuarioAutenticado = usuariosServiceImpl.findByEmail(emailAutenticado);

        // Verificar que el usuario solo edite su propio perfil (excepto ADMIN)
        boolean esAdmin = usuarioAutenticado.getRol().getNombre().equals("ROLE_ADMINISTRADOR");
        if (!esAdmin && !usuarioAutenticado.getId().equals(id)) {
            throw new RuntimeException("No tienes permiso para editar este usuario");
        }

        usuario.setId(id);

        if (usuario.getContrasena()!= null && !usuario.getContrasena().trim().isEmpty()) {
            usuario.setContrasena(BCrypt.hashpw(usuario.getContrasena(), BCrypt.gensalt()));

        } else {
            usuario.setContrasena(usuarioAutenticado.getContrasena());
        }
        return usuariosServiceImpl.actualizarUsuario(usuario);
    }

    @PutMapping("/{id}/inactivate")
    public void inactivateUsuario(@PathVariable Integer id, Authentication authentication) {
        // Validar autenticación
        if (authentication == null || authentication.getName() == null) {
            throw new RuntimeException("Usuario no autenticado");
        }

        // Obtener el usuario autenticado
        String emailAutenticado = authentication.getName();
        Usuarios usuarioAutenticado = usuariosServiceImpl.findByEmail(emailAutenticado);

        // Solo ADMIN puede inactivar usuarios
        boolean esAdmin = usuarioAutenticado.getRol().getNombre().equals("ROLE_ADMINISTRADOR");
        if (!esAdmin) {
            throw new RuntimeException("Solo los administradores pueden inactivar usuarios");
        }

        usuariosServiceImpl.inactivarUsuario(id);
    }

    @PutMapping("/{id}/activate")
    public void activateUsuario(@PathVariable Integer id, Authentication authentication) {
        // Validar autenticación
        if (authentication == null || authentication.getName() == null) {
            throw new RuntimeException("Usuario no autenticado");
        }

        // Obtener el usuario autenticado
        String emailAutenticado = authentication.getName();
        Usuarios usuarioAutenticado = usuariosServiceImpl.findByEmail(emailAutenticado);
        // Solo ADMIN puede activar usuarios
        boolean esAdmin = usuarioAutenticado.getRol().getNombre().equals("ROLE_ADMINISTRADOR");
        if (!esAdmin) {
            throw new RuntimeException("Solo los administradores pueden activar usuarios");
        }

        usuariosServiceImpl.activarUsuario(id);
    }
}
