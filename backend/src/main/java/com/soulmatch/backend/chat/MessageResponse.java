
package com.soulmatch.backend.chat;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO de respuesta que representa un mensaje de chat.
 */
public record MessageResponse(
        UUID id,
        UUID conversationId,
        UUID senderId,
        String content,
        Instant createdAt,
        Instant readAt
) {

    /**
     * Convierte una entidad Message en una respuesta para la API.
     */
    public static MessageResponse fromEntity(Message message) {
        return new MessageResponse(
                message.getId(),
                message.getConversation().getId(),
                message.getSender().getId(),
                message.getContent(),
                message.getCreatedAt(),
                message.getReadAt()
        );
    }
}
