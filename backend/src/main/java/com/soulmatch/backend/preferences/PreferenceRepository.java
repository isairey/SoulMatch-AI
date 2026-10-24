
package com.soulmatch.backend.preferences;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PreferenceRepository extends JpaRepository<Preference, UUID> {

    /**
     * Busca las preferencias de un usuario por su identificador.
     */
    Optional<Preference> findByUserId(UUID userId);

    /**
     * Comprueba si un usuario ya tiene preferencias registradas.
     */
    boolean existsByUserId(UUID userId);

    /**
     * Elimina las preferencias asociadas a un usuario.
     */
    void deleteByUserId(UUID userId);
}
