
package com.soulmatch.backend.questionnaires;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record QuestionnaireRequest(

        @NotBlank(message = "El título es obligatorio.")
        @Size(max = 200, message = "El título no puede superar los 200 caracteres.")
        String title,

        @Size(max = 5000, message = "La descripción no puede superar los 5000 caracteres.")
        String description,

        @Size(max = 100, message = "La categoría no puede superar los 100 caracteres.")
        String category,

        Boolean active,

        @Min(value = 0, message = "El orden de visualización no puede ser negativo.")
        Integer displayOrder

) {
}
