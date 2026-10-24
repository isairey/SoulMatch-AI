
package com.soulmatch.backend.preferences;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PreferenceRequest(

        @NotNull(message = "La edad mínima es obligatoria")
        @Min(value = 18, message = "La edad mínima debe ser de al menos 18 años")
        @Max(value = 99, message = "La edad mínima no puede superar los 99 años")
        Integer minAge,

        @NotNull(message = "La edad máxima es obligatoria")
        @Min(value = 18, message = "La edad máxima debe ser de al menos 18 años")
        @Max(value = 99, message = "La edad máxima no puede superar los 99 años")
        Integer maxAge,

        @NotNull(message = "La distancia máxima es obligatoria")
        @Min(value = 1, message = "La distancia mínima debe ser de 1 km")
        @Max(value = 500, message = "La distancia máxima no puede superar los 500 km")
        Integer maxDistanceKm,

        @Size(max = 30, message = "El campo de interés no puede superar los 30 caracteres")
        String interestedIn,

        @NotNull(message = "Debes indicar si deseas aparecer en las búsquedas")
        Boolean showMe

) {

    /**
     * Valida que el rango de edad sea coherente.
     */
    public boolean hasValidAgeRange() {
        return minAge != null
                && maxAge != null
                && minAge <= maxAge;
    }
}
