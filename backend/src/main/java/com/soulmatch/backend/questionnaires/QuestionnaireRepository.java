
package com.soulmatch.backend.questionnaires;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuestionnaireRepository
        extends JpaRepository<Questionnaire, UUID> {

    /**
     * Obtiene todos los cuestionarios activos,
     * ordenados por su posición de visualización.
     */
    List<Questionnaire> findAllByActiveTrueOrderByDisplayOrderAsc();

    /**
     * Busca un cuestionario activo por su identificador.
     */
    Optional<Questionnaire> findByIdAndActiveTrue(UUID id);

    /**
     * Busca cuestionarios activos por categoría,
     * ignorando mayúsculas y minúsculas.
     */
    List<Questionnaire> findAllByCategoryIgnoreCaseAndActiveTrueOrderByDisplayOrderAsc(
            String category
    );

    /**
     * Comprueba si existe un cuestionario con ese título.
     */
    boolean existsByTitleIgnoreCase(String title);

    /**
     * Obtiene cuestionarios por categoría, incluidos los inactivos.
     */
    List<Questionnaire> findAllByCategoryIgnoreCase(String category);

    /**
     * Obtiene todos los cuestionarios, activos e inactivos,
     * ordenados por posición.
     */
    List<Questionnaire> findAllByOrderByDisplayOrderAsc();
}
