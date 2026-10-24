
package com.soulmatch.backend.likes;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record LikeRequest(

        @NotNull(message = "El identificador del usuario destinatario es obligatorio.")
        UUID targetUserId

) {
}
