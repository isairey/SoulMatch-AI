
package com.soulmatch.backend.interests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record InterestRequest(

        @NotBlank(message = "El nombre del interés es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
        String name,

        @Size(max = 500, message = "La descripción no puede superar los 500 caracteres")
        String description,

        @Size(max = 50, message = "La categoría no puede superar los 50 caracteres")
        String category,

        @Size(max = 100, message = "El icono no puede superar los 100 caracteres")
        String icon

) {
}
