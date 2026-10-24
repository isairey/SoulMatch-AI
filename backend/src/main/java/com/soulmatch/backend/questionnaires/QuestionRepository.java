
package com.soulmatch.backend.questionnaires;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuestionRepository extends JpaRepository<Question, UUID> {

    /**
     * Obtiene las preguntas activas de un cuestionario,
     * ordenadas por su posición de visualización.
     */
    List<Question> findAllByQuestionnaire_IdAndActiveTrueOrderByDisplayOrderAsc(
            UUID questionnaireId
    );

    /**
     * Busca una pregunta activa por su identificador.
     */
    Optional<Question> findByIdAndActiveTrue(UUID id);

    /**
     * Obtiene todas las preguntas de un cuestionario,
     * incluidas las inactivas.
     */
    List<Question> findAllByQuestionnaire_IdOrderByDisplayOrderAsc(
            UUID questionnaireId
    );

    /**
     * Obtiene las preguntas activas de una categoría.
     */
    List<Question> findAllByCategoryIgnoreCaseAndActiveTrueOrderByDisplayOrderAsc(
            String category
    );

    /**
     * Comprueba si un cuestionario tiene preguntas activas.
     */
    boolean existsByQuestionnaire_IdAndActiveTrue(UUID questionnaireId);

    /**
     * Cuenta las preguntas activas de un cuestionario.
     */
    long countByQuestionnaire_IdAndActiveTrue(UUID questionnaireId);
}
