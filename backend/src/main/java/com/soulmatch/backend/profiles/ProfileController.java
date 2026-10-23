package com.soulmatch.backend.profiles;

import com.soulmatch.backend.users.User;
import com.soulmatch.backend.users.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/profiles")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    /**
     * Obtener todos los perfiles.
     */
    @GetMapping
    public List<ProfileResponse> getAllProfiles() {
        return profileService.getAllProfiles();
    }

    /**
     * Obtener un perfil por su ID.
     */
    @GetMapping("/{id}")
    public ProfileResponse getProfileById(@PathVariable UUID id) {
        return profileService.getProfileById(id);
    }

    /**
     * Obtener el perfil asociado a un usuario.
     */
    @GetMapping("/user/{userId}")
    public ProfileResponse getProfileByUserId(
            @PathVariable UUID userId
    ) {
        return profileService.getProfileByUserId(userId);
    }

    /**
     * Crear un perfil para un usuario.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProfileResponse createProfile(
            @Valid @RequestBody ProfileRequest request
    ) {
        return profileService.createProfile(request);
    }

    /**
     * Actualizar un perfil existente.
     */
    @PutMapping("/{id}")
    public ProfileResponse updateProfile(
            @PathVariable UUID id,
            @Valid @RequestBody ProfileRequest request
    ) {
        return profileService.updateProfile(id, request);
    }

    /**
     * Eliminar un perfil.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProfile(@PathVariable UUID id) {
        profileService.deleteProfile(id);
    }
}