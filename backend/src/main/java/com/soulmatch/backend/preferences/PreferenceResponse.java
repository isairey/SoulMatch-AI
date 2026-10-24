
package com.soulmatch.backend.preferences;

import java.time.Instant;
import java.util.UUID;

public record PreferenceResponse(
        UUID id,
        UUID userId,
        Integer minAge,
        Integer maxAge,
        Integer maxDistanceKm,
        String interestedIn,
        Boolean showMe,
        Instant createdAt,
        Instant updatedAt
) {

    /**
     * Convierte una entidad Preference en una respuesta para la API.
     */
    public static PreferenceResponse fromEntity(Preference preference) {
        if (preference == null) {
            throw new IllegalArgumentException(
                    "La preferencia no puede ser null"
            );
        }

        return new PreferenceResponse(
                preference.getId(),
                preference.getUserId(),
                preference.getMinAge(),
                preference.getMaxAge(),
                preference.getMaxDistanceKm(),
                preference.getInterestedIn(),
                preference.getShowMe(),
                preference.getCreatedAt(),
                preference.getUpdatedAt()
        );
    }
}
