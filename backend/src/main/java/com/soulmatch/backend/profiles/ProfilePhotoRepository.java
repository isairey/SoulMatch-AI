
package com.soulmatch.backend.profiles;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfilePhotoRepository
        extends JpaRepository<ProfilePhoto, UUID> {

    // Obtener todas las fotos de un perfil, ordenadas para mostrarlas.
    List<ProfilePhoto> findAllByProfile_IdOrderByDisplayOrderAsc(
            UUID profileId
    );

    // Obtener la foto principal de un perfil.
    Optional<ProfilePhoto> findByProfile_IdAndPrimaryTrue(
            UUID profileId
    );

    // Comprobar si un perfil ya tiene fotografías.
    boolean existsByProfile_Id(UUID profileId);

    // Contar las fotografías asociadas a un perfil.
    long countByProfile_Id(UUID profileId);

    // Eliminar todas las fotografías de un perfil.
    void deleteAllByProfile_Id(UUID profileId);
}
