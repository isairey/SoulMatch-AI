
package com.soulmatch.backend.likes;

import java.time.Instant;
import java.util.UUID;

public record LikeResponse(
        UUID id,
        UUID senderId,
        UUID receiverId,
        Instant createdAt
) {

    /**
     * Convierte una entidad Like en un DTO de respuesta.
     */
    public static LikeResponse fromEntity(Like like) {
        return new LikeResponse(
                like.getId(),
                like.getSender().getId(),
                like.getReceiver().getId(),
                like.getCreatedAt()
        );
    }
}
