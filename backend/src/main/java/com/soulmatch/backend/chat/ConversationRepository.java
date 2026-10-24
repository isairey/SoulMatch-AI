
package com.soulmatch.backend.chat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConversationRepository
        extends JpaRepository<Conversation, UUID> {

    /**
     * Busca las conversaciones donde participa un usuario,
     * ordenadas desde la más reciente.
     */
    List<Conversation>
    findAllByParticipantOne_IdOrParticipantTwo_IdOrderByUpdatedAtDesc(
            UUID participantOneId,
            UUID participantTwoId
    );

    /**
     * Busca una conversación por su identificador y sus participantes.
     */
    Optional<Conversation>
    findByIdAndParticipantOne_IdOrIdAndParticipantTwo_Id(
            UUID conversationIdOne,
            UUID participantOneId,
            UUID conversationIdTwo,
            UUID participantTwoId
    );

    /**
     * Comprueba si existe una conversación asociada a un match.
     */
    boolean existsByMatch_Id(UUID matchId);

    /**
     * Busca una conversación asociada a un match.
     */
    Optional<Conversation> findByMatch_Id(UUID matchId);

    /**
     * Cuenta las conversaciones de un usuario.
     */
    long countByParticipantOne_IdOrParticipantTwo_Id(
            UUID participantOneId,
            UUID participantTwoId
    );
}
