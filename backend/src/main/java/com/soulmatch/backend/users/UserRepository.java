package com.soulmatch.backend.users;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Busca un usuario por su correo electrónico, ignorando mayúsculas.
     */
    Optional<User> findByEmailIgnoreCase(String email);

    /**
     * Comprueba si ya existe una cuenta con ese correo.
     */
    boolean existsByEmailIgnoreCase(String email);

    /**
     * Busca usuarios por estado.
     */
    java.util.List<User> findAllByStatus(String status);

    /**
     * Busca usuarios por rol.
     */
    java.util.List<User> findAllByRole(String role);
}