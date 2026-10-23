package com.soulmatch.backend.users;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Comprueba que el endpoint de usuarios está disponible.
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("User API is running");
    }

    /**
     * Obtiene todos los usuarios sin exponer información sensible.
     */
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userRepository.findAll()
                .stream()
                .map(UserResponse::from)
                .toList();

        return ResponseEntity.ok(users);
    }

    /**
     * Busca un usuario por su UUID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable UUID id
    ) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuario no encontrado"
                ));

        return ResponseEntity.ok(UserResponse.from(user));
    }

    /**
     * DTO de respuesta: excluye passwordHash.
     */
    public record UserResponse(
            UUID id,
            String email,
            String role,
            String status,
            boolean emailVerified,
            java.time.Instant createdAt,
            java.time.Instant updatedAt
    ) {
        public static UserResponse from(User user) {
            return new UserResponse(
                    user.getId(),
                    user.getEmail(),
                    user.getRole(),
                    user.getStatus(),
                    user.isEmailVerified(),
                    user.getCreatedAt(),
                    user.getUpdatedAt()
            );
        }
    }
}