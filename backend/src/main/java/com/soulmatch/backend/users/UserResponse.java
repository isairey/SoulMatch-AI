package com.soulmatch.backend.users;

import java.time.Instant;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        String role,
        String status,
        boolean emailVerified,
        Instant createdAt,
        Instant updatedAt
) {

    /**
     * Convierte una entidad User en una respuesta segura para la API.
     */
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