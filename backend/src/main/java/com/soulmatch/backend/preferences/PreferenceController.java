
package com.soulmatch.backend.preferences;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/preferences")
public class PreferenceController {

    private final PreferenceService preferenceService;

    public PreferenceController(PreferenceService preferenceService) {
        this.preferenceService = preferenceService;
    }

    /**
     * Obtiene las preferencias de un usuario.
     */
    @GetMapping("/{userId}")
    public PreferenceResponse getPreferences(@PathVariable UUID userId) {
        return preferenceService.getPreferences(userId);
    }

    /**
     * Crea las preferencias de un usuario.
     */
    @PostMapping("/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public PreferenceResponse createPreferences(
            @PathVariable UUID userId,
            @Valid @RequestBody PreferenceRequest request) {

        return preferenceService.createPreferences(userId, request);
    }

    /**
     * Actualiza las preferencias de un usuario.
     */
    @PutMapping("/{userId}")
    public PreferenceResponse updatePreferences(
            @PathVariable UUID userId,
            @Valid @RequestBody PreferenceRequest request) {

        return preferenceService.updatePreferences(userId, request);
    }

    /**
     * Elimina las preferencias de un usuario.
     */
    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePreferences(@PathVariable UUID userId) {
        preferenceService.deletePreferences(userId);
    }
}
