
package com.soulmatch.backend.preferences;

import com.soulmatch.backend.users.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class PreferenceService {

    private final PreferenceRepository preferenceRepository;
    private final UserRepository userRepository;

    public PreferenceService(
            PreferenceRepository preferenceRepository,
            UserRepository userRepository
    ) {
        this.preferenceRepository = preferenceRepository;
        this.userRepository = userRepository;
    }

    /**
     * Obtiene las preferencias de un usuario.
     */
    @Transactional(readOnly = true)
    public PreferenceResponse getPreferences(UUID userId) {
        validateUserExists(userId);

        Preference preference = preferenceRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "El usuario todavía no tiene preferencias registradas"
                ));

        return PreferenceResponse.fromEntity(preference);
    }

    /**
     * Crea las preferencias de un usuario.
     */
    @Transactional
    public PreferenceResponse createPreferences(
            UUID userId,
            PreferenceRequest request
    ) {
        validateUserExists(userId);
        validateRequest(request);

        if (preferenceRepository.existsByUserId(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El usuario ya tiene preferencias registradas"
            );
        }

        Preference preference = new Preference();
        preference.setUserId(userId);

        applyRequest(preference, request);

        Preference savedPreference = preferenceRepository.save(preference);

        return PreferenceResponse.fromEntity(savedPreference);
    }

    /**
     * Actualiza las preferencias existentes de un usuario.
     */
    @Transactional
    public PreferenceResponse updatePreferences(
            UUID userId,
            PreferenceRequest request
    ) {
        validateUserExists(userId);
        validateRequest(request);

        Preference preference = preferenceRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No se encontraron preferencias para este usuario"
                ));

        applyRequest(preference, request);

        Preference updatedPreference = preferenceRepository.save(preference);

        return PreferenceResponse.fromEntity(updatedPreference);
    }

    /**
     * Elimina las preferencias de un usuario.
     */
    @Transactional
    public void deletePreferences(UUID userId) {
        validateUserExists(userId);

        Preference preference = preferenceRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No se encontraron preferencias para eliminar"
                ));

        preferenceRepository.delete(preference);
    }

    /**
     * Comprueba que el usuario exista en la base de datos.
     */
    private void validateUserExists(UUID userId) {
        if (userId == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El identificador del usuario es obligatorio"
            );
        }

        if (!userRepository.existsById(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "El usuario no existe"
            );
        }
    }

    /**
     * Valida los datos recibidos.
     */
    private void validateRequest(PreferenceRequest request) {
        if (request == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Los datos de las preferencias son obligatorios"
            );
        }

        if (!request.hasValidAgeRange()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La edad mínima no puede ser mayor que la edad máxima"
            );
        }
    }

    /**
     * Copia los datos de la solicitud a la entidad.
     */
    private void applyRequest(
            Preference preference,
            PreferenceRequest request
    ) {
        preference.setMinAge(request.minAge());
        preference.setMaxAge(request.maxAge());
        preference.setMaxDistanceKm(request.maxDistanceKm());
        preference.setInterestedIn(
                request.interestedIn() == null
                        ? null
                        : request.interestedIn().trim()
        );
        preference.setShowMe(request.showMe());
    }
}