
package com.soulmatch.backend.interests;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/interests")
public class InterestController {

    private final InterestService interestService;

    public InterestController(InterestService interestService) {
        this.interestService = interestService;
    }

    /**
     * Obtiene todos los intereses activos.
     */
    @GetMapping
    public List<InterestResponse> getAllInterests() {
        return interestService.getAllActiveInterests();
    }

    /**
     * Obtiene un interés por su identificador.
     */
    @GetMapping("/{id}")
    public InterestResponse getInterestById(@PathVariable UUID id) {
        return interestService.getInterestById(id);
    }

    /**
     * Busca intereses por nombre.
     */
    @GetMapping("/search")
    public List<InterestResponse> searchInterests(
            @RequestParam String name
    ) {
        return interestService.searchInterests(name);
    }

    /**
     * Filtra intereses por categoría.
     */
    @GetMapping("/category/{category}")
    public List<InterestResponse> getInterestsByCategory(
            @PathVariable String category
    ) {
        return interestService.getInterestsByCategory(category);
    }

    /**
     * Crea un interés nuevo.
     * Diseñado para uso administrativo.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InterestResponse createInterest(
            @Valid @RequestBody InterestRequest request
    ) {
        return interestService.createInterest(request);
    }

    /**
     * Actualiza un interés existente.
     * Diseñado para uso administrativo.
     */
    @PutMapping("/{id}")
    public InterestResponse updateInterest(
            @PathVariable UUID id,
            @Valid @RequestBody InterestRequest request
    ) {
        return interestService.updateInterest(id, request);
    }

    /**
     * Desactiva un interés sin eliminarlo de la base de datos.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivateInterest(@PathVariable UUID id) {
        interestService.deactivateInterest(id);
    }
}
