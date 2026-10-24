
package com.soulmatch.backend.matches;

import com.soulmatch.backend.users.User;
import com.soulmatch.backend.users.UserRepository;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class MatchService {

    private final MatchRepository matchRepository;
    private final UserRepository userRepository;

    public MatchService(
            MatchRepository matchRepository,
            UserRepository userRepository
    ) {
        this.matchRepository = matchRepository;
        this.userRepository = userRepository;
    }

    /**
     * Obtiene los matches del usuario autenticado.
     */
    @Transactional(readOnly = true)
    public List<MatchResponse> getMyMatches() {
        User currentUser = getAuthenticatedUser();
        UUID userId = currentUser.getId();

        return matchRepository
                .findAllByUserOne_IdOrUserTwo_IdOrderByMatchedAtDesc(
                        userId,
                        userId
                )
                .stream()
                .filter(match -> "ACTIVE".equalsIgnoreCase(match.getStatus()))
                .map(MatchResponse::fromEntity)
                .toList();
    }

    /**
     * Obtiene un match si el usuario autenticado es participante.
     */
    @Transactional(readOnly = true)
    public MatchResponse getMatchById(UUID matchId) {
        User currentUser = getAuthenticatedUser();

        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new MatchNotFoundException(
                        "No se encontró el match solicitado."
                ));

        verifyParticipant(match, currentUser);

        return MatchResponse.fromEntity(match);
    }

    /**
     * Finaliza un match si el usuario autenticado es participante.
     */
    public MatchResponse endMatch(UUID matchId) {
        User currentUser = getAuthenticatedUser();

        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new MatchNotFoundException(
                        "No se encontró el match solicitado."
                ));

        verifyParticipant(match, currentUser);

        if (!"ACTIVE".equalsIgnoreCase(match.getStatus())) {
            throw new IllegalStateException(
                    "El match ya está finalizado o no está activo."
            );
        }

        match.setStatus("ENDED");
        match.setLastInteractionAt(java.time.Instant.now());

        Match savedMatch = matchRepository.save(match);

        return MatchResponse.fromEntity(savedMatch);
    }

    /**
     * Obtiene el usuario autenticado a partir de su correo.
     */
    private User getAuthenticatedUser() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getName() == null
                || authentication.getName().isBlank()
                || "anonymousUser".equals(authentication.getName())) {

            throw new AccessDeniedException(
                    "Debes iniciar sesión para realizar esta operación."
            );
        }

        return userRepository
                .findByEmailIgnoreCase(authentication.getName())
                .orElseThrow(() -> new AccessDeniedException(
                        "No se encontró el usuario autenticado."
                ));
    }

    /**
     * Comprueba que el usuario forme parte del match.
     */
    private void verifyParticipant(Match match, User currentUser) {
        UUID currentUserId = currentUser.getId();

        boolean isParticipant =
                match.getUserOne().getId().equals(currentUserId)
                || match.getUserTwo().getId().equals(currentUserId);

        if (!isParticipant) {
            throw new AccessDeniedException(
                    "No tienes permiso para acceder a este match."
            );
        }
    }
}

