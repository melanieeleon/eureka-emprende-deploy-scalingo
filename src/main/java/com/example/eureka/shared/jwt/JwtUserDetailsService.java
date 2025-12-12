package com.example.eureka.shared.jwt;

import com.example.eureka.auth.domain.Usuarios;
import com.example.eureka.auth.port.out.IUserRepository;
import com.example.eureka.shared.util.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtUserDetailsService implements UserDetailsService {

    private final IUserRepository repo;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuarios usuarios = repo.obtenerUsuarioCorreo(username);

        if (usuarios == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        List<GrantedAuthority> roles = new ArrayList<>();

        if (usuarios.getRol() != null) {
            String roleName = usuarios.getRol().getNombre();
            if (!roleName.startsWith("ROLE_")) {
                roleName = "ROLE_" + roleName;
            }
            roles.add(new SimpleGrantedAuthority(roleName));
        } else {
            throw new UsernameNotFoundException("Usuario sin rol asignado: " + username);
        }

        // Devolver CustomUserDetails en lugar de User
        return new CustomUserDetails(usuarios, roles);
    }
}


/*public class JwtUserDetailsService implements UserDetailsService {

    private final IUserRepository repo;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuarios usuarios = repo.obtenerUsuarioCorreo(username);

        if(usuarios == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        // Obtener el rol único del usuario
        List<GrantedAuthority> roles = new ArrayList<>();

        // Si el usuario tiene un rol asignado
        if(usuarios.getRol() != null) {
            String roleName = usuarios.getRol().getNombre();

            // Asegúrate de que el nombre del rol tenga el prefijo "ROLE_"
            if(!roleName.startsWith("ROLE_")) {
                roleName = "ROLE_" + roleName;
            }

            roles.add(new SimpleGrantedAuthority(roleName));
            log.info("Usuario {} tiene rol: {}", username, roleName);
        } else {
            // Si no tiene rol, denegar acceso lanzando excepción
            log.error("Usuario {} sin rol asignado - acceso denegado", username);
            throw new UsernameNotFoundException("Usuario sin rol asignado: " + username);
        }

        return new org.springframework.security.core.userdetails.User(
                usuarios.getCorreo(),
                usuarios.getContrasena(),
                roles
        );
    }
}*/

/*
SUPOSICIONES:
- Tu clase Usuarios tiene: private Rol rol (relación One-to-One o Many-to-One)
- Tu clase Rol tiene: String nombre (con valores como "ADMINISTRADOR", "EMPRENDEDOR")

EJEMPLO DE ESTRUCTURA:

@Entity
public class Usuarios {
    @Id
    private Long id;
    private String correo;
    private String contrasena;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rol_id")
    private Rol rol;  // ← UN SOLO ROL

    // getters y setters...
}

@Entity
public class Rol {
    @Id
    private Long id;
    private String nombre;  // "ADMINISTRADOR" o "EMPRENDEDOR"
}

EJEMPLO DE USO EN CONTROLADORES:

@GetMapping("/solo-admin")
@PreAuthorize("hasRole('ADMINISTRADOR')")
public ResponseEntity<String> soloAdmin() {
    return ResponseEntity.ok("Solo administradores");
}

@GetMapping("/solo-emprendedor")
@PreAuthorize("hasRole('EMPRENDEDOR')")
public ResponseEntity<String> soloEmprendedor() {
    return ResponseEntity.ok("Solo emprendedores");
}

@GetMapping("/mi-rol")
public ResponseEntity<String> miRol() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String rol = auth.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .findFirst()
        .orElse("SIN ROL");

    return ResponseEntity.ok("Tu rol: " + rol);
}
*/