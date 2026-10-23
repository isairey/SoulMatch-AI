
package com.soulmatch.backend.profiles;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

public record ProfileRequest(

        @NotBlank(message = "El ID del usuario es obligatorio")
        String userId,

        @NotBlank(message = "El nombre para mostrar es obligatorio")
        @Size(max = 80, message = "El nombre no puede superar los 80 caracteres")
        String displayName,

        @Past(message = "La fecha de nacimiento debe ser anterior a hoy")
        LocalDate birthDate,

        @Size(max = 500, message = "La biografía no puede superar los 500 caracteres")
        String biography,

        @Size(max = 40, message = "El género no puede superar los 40 caracteres")
        String gender,

        @Size(max = 120, message = "La ciudad no puede superar los 120 caracteres")
        String city,

        @Size(max = 120, message = "El país no puede superar los 120 caracteres")
        String country,

        @Size(max = 120, message = "La ocupación no puede superar los 120 caracteres")
        String occupation,

        @Size(max = 120, message = "El nivel educativo no puede superar los 120 caracteres")
        String education

) {
}
