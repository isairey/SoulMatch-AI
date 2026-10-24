
package com.soulmatch.backend.interests;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InterestRepository extends JpaRepository<Interest, UUID> {

    /**
     * Busca un interés por su nombre exacto.
     */
    Optional<Interest> findByNameIgnoreCase(String name);

    /**
     * Comprueba si ya existe un interés con ese nombre.
     */
    boolean existsByNameIgnoreCase(String name);

    /**
     * Obtiene los intereses activos.
     */
    List<Interest> findAllByActiveTrue();

    /**
     * Busca intereses activos por categoría.
     */
    List<Interest> findAllByCategoryIgnoreCaseAndActiveTrue(String category);

    /**
     * Busca intereses activos cuyo nombre contenga el texto indicado.
     */
    List<Interest> findAllByNameContainingIgnoreCaseAndActiveTrue(String name);

    /**
     * Obtiene intereses por categoría, estén activos o no.
     */
    List<Interest> findAllByCategoryIgnoreCase(String category);
}
