package com.usersystem.sistemausuariosbackend.controller;

import com.usersystem.sistemausuariosbackend.model.User;
import com.usersystem.sistemausuariosbackend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Importa esto
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication; // <-- ¡Añade esta importación!

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users") // Ruta base para este controlador
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Endpoint para obtener todos los usuarios (requiere ROLE_ADMIN)
    // La restricción de rol ya la pusimos en WebSecurityConfig, pero también se puede usar @PreAuthorize
    @GetMapping
    // @PreAuthorize("hasRole('ADMIN')") // Opcional: También se puede proteger a nivel de metodo
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    // Endpoint para obtener un usuario por ID (requiere ROLE_ADMIN)
    @GetMapping("/{id}")
    // @PreAuthorize("hasRole('ADMIN')") // Opcional
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userRepository.findById(id);
        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Puedes añadir más endpoints aquí: update, delete, etc.
    // Por ejemplo, un endpoint para que un usuario vea su propio perfil:
    /*
    @GetMapping("/me")
    public ResponseEntity<User> getMyProfile(Authentication authentication) {
        String username = authentication.getName(); // Obtiene el username del token
        return userRepository.findByUsername(username)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
    }
    */

    // --- ¡AÑADE ESTE NUEVO ENDPOINT! ---
    @GetMapping("/me")
    public ResponseEntity<User> getMyProfile(Authentication authentication) {
        // El objeto 'authentication' es inyectado automáticamente por Spring Security
        // Contiene los detalles del usuario autenticado actualmente
        String username = authentication.getName(); // Obtiene el nombre de usuario del principal autenticado

        // Busca el usuario en la base de datos usando el nombre de usuario
        Optional<User> user = userRepository.findByUsername(username);

        // Si el usuario existe, lo devuelve; de lo contrario, devuelve 404 Not Found
        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    // --- FIN DEL NUEVO ENDPOINT ---
}