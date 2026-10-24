
package com.soulmatch.backend.interests;

import java.time.Instant;
import java.util.UUID;

public record InterestResponse(
        UUID id,
        String name,
        String description,
        String category,
        String icon,
        Boolean active,
        Instant createdAt
) {

    /**
     * Convierte una entidad Interest en una respuesta para la API.
     */
    public static InterestResponse fromEntity(Interest interest) {
        if (interest == null) {
            throw new IllegalArgumentException(
                    "El interés no puede ser null"
            );
        }

        return new InterestResponse(
                interest.getId(),
                interest.getName(),
                interest.getDescription(),
                interest.getCategory(),
                interest.getIcon(),
                interest.getActive(),
                interest.getCreatedAt()
        );
    }
}
