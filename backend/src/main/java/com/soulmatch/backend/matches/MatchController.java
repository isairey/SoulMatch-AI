
package com.soulmatch.backend.matches;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/matches")
public class MatchController {

    private final MatchService matchService;

    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    /**
     * Obtiene los matches del usuario autenticado.
     */
    @GetMapping
    public List<MatchResponse> getMyMatches() {
        return matchService.getMyMatches();
    }

    /**
     * Obtiene un match específico.
     */
    @GetMapping("/{matchId}")
    public MatchResponse getMatchById(
            @PathVariable UUID matchId
    ) {
        return matchService.getMatchById(matchId);
    }

    /**
     * Finaliza un match.
     */
    @PatchMapping("/{matchId}/end")
    public MatchResponse endMatch(
            @PathVariable UUID matchId
    ) {
        return matchService.endMatch(matchId);
    }

    /**
     * Registra una coincidencia.
     * Este endpoint debe estar restringido a la lógica
     * interna de matching y no permitir matches arbitrarios.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MatchResponse createMatch(
            @Valid @RequestBody CreateMatchRequest request
    ) {
        return matchService.createMatch(request);
    }
}
