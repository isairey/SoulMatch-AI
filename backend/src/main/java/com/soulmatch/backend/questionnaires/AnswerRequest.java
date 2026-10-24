
package com.soulmatch.backend.questionnaires;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record AnswerRequest(

        @NotNull(message = "El identificador de la pregunta es obligatorio")
        UUID questionId,

        @Size(
                max = 5000,
                message = "La respuesta no puede superar los 5000 caracteres"
        )
        String answerText,

        @Size(
                max = 255,
                message = "La opción seleccionada no puede superar los 255 caracteres"
        )
        String selectedOption

) {

    /**
     * Comprueba que se haya proporcionado una respuesta.
     */
    public boolean hasAnswer() {
        return (answerText != null && !answerText.isBlank())
                || (selectedOption != null && !selectedOption.isBlank());
    }
}

