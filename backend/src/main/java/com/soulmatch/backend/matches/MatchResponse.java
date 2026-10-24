
package com.soulmatch.backend.matches;

import java.time.Instant;
import java.util.UUID;

public record MatchResponse(
        UUID id,
        UUID userOneId,
        UUID userTwoId,
        String status,
        Instant matchedAt,
        Instant lastInteractionAt
) {

    /**
     * Convierte una entidad Match en un DTO de respuesta.
     */
    public static MatchResponse fromEntity(Match match) {
        return new MatchResponse(
                match.getId(),
                match.getUserOne().getId(),
                match.getUserTwo().getId(),
                match.getStatus(),
                match.getMatchedAt(),
                match.getLastInteractionAt()
        );
    }
}
