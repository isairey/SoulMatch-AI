
package com.soulmatch.backend.chat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO de entrada para enviar un mensaje en una conversación.
 */
public record MessageRequest(

        @NotBlank(message = "El contenido del mensaje es obligatorio")
        @Size(
                max = 5000,
                message = "El mensaje no puede superar los 5000 caracteres"
        )
        String content

) {
}
