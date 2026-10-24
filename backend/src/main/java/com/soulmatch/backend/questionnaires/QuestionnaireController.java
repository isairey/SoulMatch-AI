
package com.soulmatch.backend.questionnaires;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/questionnaires")
public class QuestionnaireController {

    private final QuestionnaireService questionnaireService;

    public QuestionnaireController(
            QuestionnaireService questionnaireService
    ) {
        this.questionnaireService = questionnaireService;
    }

    /**
     * Obtiene todos los cuestionarios activos.
     */
    @GetMapping
    public List<QuestionnaireResponse> getAllQuestionnaires() {
        return questionnaireService.getAllActiveQuestionnaires();
    }

    /**
     * Obtiene un cuestionario por su identificador.
     */
    @GetMapping("/{id}")
    public QuestionnaireResponse getQuestionnaireById(
            @PathVariable UUID id
    ) {
        return questionnaireService.getQuestionnaireById(id);
    }

    /**
     * Filtra cuestionarios activos por categoría.
     */
    @GetMapping("/category/{category}")
    public List<QuestionnaireResponse> getByCategory(
            @PathVariable String category
    ) {
        return questionnaireService.getQuestionnairesByCategory(category);
    }

    /**
     * Crea un cuestionario nuevo.
     * Debe restringirse a usuarios administradores.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public QuestionnaireResponse createQuestionnaire(
            @Valid @RequestBody QuestionnaireRequest request
    ) {
        return questionnaireService.createQuestionnaire(request);
    }

    /**
     * Actualiza un cuestionario existente.
     * Debe restringirse a usuarios administradores.
     */
    @PutMapping("/{id}")
    public QuestionnaireResponse updateQuestionnaire(
            @PathVariable UUID id,
            @Valid @RequestBody QuestionnaireRequest request
    ) {
        return questionnaireService.updateQuestionnaire(id, request);
    }

    /**
     * Desactiva un cuestionario sin eliminarlo físicamente.
     * Debe restringirse a usuarios administradores.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivateQuestionnaire(@PathVariable UUID id) {
        questionnaireService.deactivateQuestionnaire(id);
    }
}
