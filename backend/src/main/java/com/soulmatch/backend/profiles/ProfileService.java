
package com.soulmatch.backend.profiles;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.soulmatch.backend.users.User;
import com.soulmatch.backend.users.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;

    public ProfileService(
            ProfileRepository profileRepository,
            UserRepository userRepository
    ) {
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
    }

    /**
     * Obtener todos los perfiles.
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<ProfileResponse> getAllProfiles() {
        return profileRepository.findAll()
                .stream()
                .map(ProfileResponse::from)
                .toList();
    }

    /**
     * Obtener un perfil por su ID.
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    public ProfileResponse getProfileById(UUID id) {
        Profile profile = findProfileById(id);
        return ProfileResponse.from(profile);
    }

    /**
     * Obtener el perfil asociado a un usuario.
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    public ProfileResponse getProfileByUserId(UUID userId) {
        Profile profile = profileRepository.findByUser_Id(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No se encontró un perfil para el usuario indicado"
                ));

        return ProfileResponse.from(profile);
    }

    /**
     * Crear un perfil nuevo.
     */
    @Transactional
    public ProfileResponse createProfile(ProfileRequest request) {
        UUID userId = parseUserId(request.userId());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "El usuario indicado no existe"
                ));

        if (profileRepository.existsByUser_Id(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Este usuario ya tiene un perfil"
            );
        }

        Profile profile = new Profile();
        profile.setUser(user);

        applyRequest(profile, request);

        Profile savedProfile = profileRepository.save(profile);

        return ProfileResponse.from(savedProfile);
    }

    /**
     * Actualizar un perfil existente.
     */
    @Transactional
    public ProfileResponse updateProfile(
            UUID id,
            ProfileRequest request
    ) {
        Profile profile = findProfileById(id);

        // El usuario asociado no se cambia durante una actualización.
        applyRequest(profile, request);

        Profile updatedProfile = profileRepository.save(profile);

        return ProfileResponse.from(updatedProfile);
    }

    /**
     * Eliminar un perfil por su ID.
     */
    @Transactional
    public void deleteProfile(UUID id) {
        Profile profile = findProfileById(id);
        profileRepository.delete(profile);
    }

    /**
     * Aplicar los campos editables de la solicitud.
     */
    private void applyRequest(
            Profile profile,
            ProfileRequest request
    ) {
        profile.setDisplayName(request.displayName().trim());
        profile.setBirthDate(request.birthDate());
        profile.setBiography(normalizeOptional(request.biography()));
        profile.setGender(normalizeOptional(request.gender()));
        profile.setCity(normalizeOptional(request.city()));

        String country = normalizeOptional(request.country());
        profile.setCountry(
                country == null ? "Mexico" : country
        );

        profile.setOccupation(normalizeOptional(request.occupation()));
        profile.setEducation(normalizeOptional(request.education()));
    }

    /**
     * Buscar un perfil por ID o devolver un error 404.
     */
    private Profile findProfileById(UUID id) {
        return profileRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No se encontró el perfil solicitado"
                ));
    }

    /**
     * Convertir y validar el ID del usuario.
     */
    private UUID parseUserId(String userId) {
        try {
            return UUID.fromString(userId);
        } catch (IllegalArgumentException | NullPointerException exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El ID del usuario no es un UUID válido"
            );
        }
    }

    /**
     * Convertir cadenas vacías en null.
     */
    private String normalizeOptional(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}
