
package com.soulmatch.backend.auth;

import java.time.Instant;
import java.util.UUID;

public record AuthResponse(
        String token,
        String tokenType,
        Instant expiresAt,
        UUID userId,
        String email,
        String role
) {

    public AuthResponse(
            String token,
            Instant expiresAt,
            UUID userId,
            String email,
            String role
    ) {
        this(
                token,
                "Bearer",
                expiresAt,
                userId,
                email,
                role
        );
    }
}
