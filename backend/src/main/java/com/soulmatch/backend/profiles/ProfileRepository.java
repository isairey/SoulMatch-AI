
package com.soulmatch.backend.profiles;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, UUID> {

    // Buscar un perfil por el ID del usuario asociado.
    Optional<Profile> findByUser_Id(UUID userId);

    // Comprobar si un usuario ya tiene un perfil.
    boolean existsByUser_Id(UUID userId);

    // Buscar un perfil por el nombre que muestra el usuario.
    Optional<Profile> findByDisplayNameIgnoreCase(String displayName);

    // Contar perfiles asociados a una ciudad.
    long countByCityIgnoreCase(String city);

    // Eliminar el perfil asociado a un usuario.
    void deleteByUser_Id(UUID userId);
}
