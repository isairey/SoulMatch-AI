
package com.soulmatch.backend.questionnaires;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@Transactional
public class QuestionnaireService {

    private final QuestionnaireRepository questionnaireRepository;

    public QuestionnaireService(
            QuestionnaireRepository questionnaireRepository
    ) {
        this.questionnaireRepository = questionnaireRepository;
    }

    /**
     * Obtiene todos los cuestionarios activos.
     */
    @Transactional(readOnly = true)
    public List<QuestionnaireResponse> getAllActiveQuestionnaires() {
        return questionnaireRepository
                .findAllByActiveTrueOrderByDisplayOrderAsc()
                .stream()
                .map(QuestionnaireResponse::fromEntity)
                .toList();
    }

    /**
     * Busca un cuestionario activo por su identificador.
     */
    @Transactional(readOnly = true)
    public QuestionnaireResponse getQuestionnaireById(UUID id) {
        Questionnaire questionnaire = questionnaireRepository
                .findByIdAndActiveTrue(id)
                .orElseThrow(() -> new QuestionnaireNotFoundException(
                        "No se encontró el cuestionario solicitado."
                ));

        return QuestionnaireResponse.fromEntity(questionnaire);
    }

    /**
     * Obtiene los cuestionarios activos de una categoría.
     */
    @Transactional(readOnly = true)
    public List<QuestionnaireResponse> getQuestionnairesByCategory(
            String category
    ) {
        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException(
                    "La categoría no puede estar vacía."
            );
        }

        return questionnaireRepository
                .findAllByCategoryIgnoreCaseAndActiveTrueOrderByDisplayOrderAsc(
                        category.trim()
                )
                .stream()
                .map(QuestionnaireResponse::fromEntity)
                .toList();
    }

    /**
     * Crea un cuestionario.
     */
    public QuestionnaireResponse createQuestionnaire(
            QuestionnaireRequest request
    ) {
        validateRequest(request);

        String title = normalizeTitle(request.title());

        if (questionnaireRepository.existsByTitleIgnoreCase(title)) {
            throw new IllegalArgumentException(
                    "Ya existe un cuestionario con ese título."
            );
        }

        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setTitle(title);
        questionnaire.setDescription(normalizeOptionalText(request.description()));
        questionnaire.setCategory(normalizeCategory(request.category()));
        questionnaire.setActive(
                request.active() != null ? request.active() : true
        );
        questionnaire.setDisplayOrder(
                request.displayOrder() != null ? request.displayOrder() : 0
        );

        Questionnaire saved = questionnaireRepository.save(questionnaire);

        return QuestionnaireResponse.fromEntity(saved);
    }

    /**
     * Actualiza un cuestionario existente.
     */
    public QuestionnaireResponse updateQuestionnaire(
            UUID id,
            QuestionnaireRequest request
    ) {
        validateRequest(request);

        Questionnaire questionnaire = questionnaireRepository
                .findById(id)
                .orElseThrow(() -> new QuestionnaireNotFoundException(
                        "No se encontró el cuestionario solicitado."
                ));

        String title = normalizeTitle(request.title());

        boolean duplicateTitle = questionnaireRepository
                .existsByTitleIgnoreCase(title);

        if (duplicateTitle
                && !questionnaire.getTitle().equalsIgnoreCase(title)) {
            throw new IllegalArgumentException(
                    "Ya existe otro cuestionario con ese título."
            );
        }

        questionnaire.setTitle(title);
        questionnaire.setDescription(normalizeOptionalText(request.description()));
        questionnaire.setCategory(normalizeCategory(request.category()));

        if (request.active() != null) {
            questionnaire.setActive(request.active());
        }

        if (request.displayOrder() != null) {
            questionnaire.setDisplayOrder(request.displayOrder());
        }

        Questionnaire updated = questionnaireRepository.save(questionnaire);

        return QuestionnaireResponse.fromEntity(updated);
    }

    /**
     * Desactiva un cuestionario sin eliminarlo de la base de datos.
     */
    public void deactivateQuestionnaire(UUID id) {
        Questionnaire questionnaire = questionnaireRepository
                .findById(id)
                .orElseThrow(() -> new QuestionnaireNotFoundException(
                        "No se encontró el cuestionario solicitado."
                ));

        questionnaire.setActive(false);
        questionnaireRepository.save(questionnaire);
    }

    private void validateRequest(QuestionnaireRequest request) {
        if (request == null) {
            throw new IllegalArgumentException(
                    "Los datos del cuestionario son obligatorios."
            );
        }

        if (request.title() == null || request.title().isBlank()) {
            throw new IllegalArgumentException(
                    "El título del cuestionario es obligatorio."
            );
        }

        if (request.title().trim().length() > 200) {
            throw new IllegalArgumentException(
                    "El título no puede superar los 200 caracteres."
            );
        }

        if (request.displayOrder() != null && request.displayOrder() < 0) {
            throw new IllegalArgumentException(
                    "El orden de visualización no puede ser negativo."
            );
        }
    }

    private String normalizeTitle(String title) {
        return title.trim();
    }

    private String normalizeOptionalText(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }

    private String normalizeCategory(String category) {
        if (category == null || category.isBlank()) {
            return null;
        }

        return category.trim().toLowerCase(Locale.ROOT);
    }
}
