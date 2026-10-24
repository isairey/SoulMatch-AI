
package com.soulmatch.backend.chat;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {

    /**
     * Obtiene los mensajes de una conversación, del más reciente
     * al más antiguo, con paginación.
     */
    Page<Message> findAllByConversation_IdOrderByCreatedAtDesc(
            UUID conversationId,
            Pageable pageable
    );

    /**
     * Cuenta los mensajes de una conversación.
     */
    long countByConversation_Id(UUID conversationId);

    /**
     * Cuenta los mensajes que ha enviado un usuario.
     */
    long countBySender_Id(UUID senderId);

    /**
     * Marca como leídos los mensajes de otros usuarios de una
     * conversación, excluyendo los que envió el usuario actual.
     */
    @Modifying
    @Query("""
        UPDATE Message m
        SET m.readAt = :readAt
        WHERE m.conversation.id = :conversationId
          AND m.sender.id <> :userId
          AND m.readAt IS NULL
        """)
    int markMessagesAsRead(
            @Param("conversationId") UUID conversationId,
            @Param("userId") UUID userId,
            @Param("readAt") Instant readAt
    );
}
