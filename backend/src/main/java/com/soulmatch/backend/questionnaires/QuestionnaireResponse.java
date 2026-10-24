
package com.soulmatch.backend.questionnaires;

import java.time.Instant;
import java.util.UUID;

public record QuestionnaireResponse(
        UUID id,
        String title,
        String description,
        String category,
        Boolean active,
        Integer displayOrder,
        Instant createdAt,
        Instant updatedAt
) {

    /**
     * Convierte una entidad Questionnaire en una respuesta DTO.
     */
    public static QuestionnaireResponse fromEntity(
            Questionnaire questionnaire
    ) {
        return new QuestionnaireResponse(
                questionnaire.getId(),
                questionnaire.getTitle(),
                questionnaire.getDescription(),
                questionnaire.getCategory(),
                questionnaire.getActive(),
                questionnaire.getDisplayOrder(),
                questionnaire.getCreatedAt(),
                questionnaire.getUpdatedAt()
        );
    }
}
