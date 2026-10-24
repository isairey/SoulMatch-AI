
package com.soulmatch.backend.likes;

import com.soulmatch.backend.matches.Match;
import com.soulmatch.backend.matches.MatchRepository;
import com.soulmatch.backend.users.User;
import com.soulmatch.backend.users.UserRepository;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class LikeService {

    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final MatchRepository matchRepository;

    public LikeService(
            LikeRepository likeRepository,
            UserRepository userRepository,
            MatchRepository matchRepository
    ) {
        this.likeRepository = likeRepository;
        this.userRepository = userRepository;
        this.matchRepository = matchRepository;
    }

    /**
     * Envía un like al usuario indicado.
     * Si existe un like mutuo, registra un match.
     */
    public LikeResponse createLike(UUID targetUserId) {
        if (targetUserId == null) {
            throw new IllegalArgumentException(
                    "El identificador del destinatario es obligatorio."
            );
        }

        User sender = getAuthenticatedUser();

        if (sender.getId().equals(targetUserId)) {
            throw new IllegalArgumentException(
                    "No puedes darte like a ti mismo."
            );
        }

        User receiver = userRepository.findById(targetUserId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontró el usuario destinatario."
                ));

        // Evita crear likes duplicados en solicitudes normales.
        Like like = likeRepository
                .findBySender_IdAndReceiver_Id(
                        sender.getId(),
                        receiver.getId()
                )
                .orElseGet(() -> likeRepository.save(
                        new Like(sender, receiver)
                ));

        // Si el destinatario ya dio like al emisor, existe reciprocidad.
        boolean mutualLike = likeRepository
                .existsBySender_IdAndReceiver_Id(
                        receiver.getId(),
                        sender.getId()
                );

        if (mutualLike) {
            createMatchIfAbsent(sender, receiver);
        }

        return LikeResponse.fromEntity(like);
    }

    /**
     * Obtiene los likes recibidos por el usuario autenticado.
     */
    @Transactional(readOnly = true)
    public List<LikeResponse> getReceivedLikes() {
        User user = getAuthenticatedUser();

        return likeRepository
                .findAllByReceiver_IdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(LikeResponse::fromEntity)
                .toList();
    }

    /**
     * Obtiene los likes enviados por el usuario autenticado.
     */
    @Transactional(readOnly = true)
    public List<LikeResponse> getSentLikes() {
        User user = getAuthenticatedUser();

        return likeRepository
                .findAllBySender_IdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(LikeResponse::fromEntity)
                .toList();
    }

    /**
     * Retira un like enviado por el usuario autenticado.
     */
    public void removeLike(UUID targetUserId) {
        if (targetUserId == null) {
            throw new IllegalArgumentException(
                    "El identificador del destinatario es obligatorio."
            );
        }

        User sender = getAuthenticatedUser();

        long deleted = likeRepository.deleteBySender_IdAndReceiver_Id(
                sender.getId(),
                targetUserId
        );

        if (deleted == 0) {
            throw new IllegalArgumentException(
                    "No existe un like enviado a ese usuario."
            );
        }

        // No se elimina automáticamente un match existente.
        // Retirar un like no debe borrar el historial de coincidencias.
    }

    /**
     * Consulta el estado de los likes entre el usuario autenticado
     * y el usuario indicado.
     */
    @Transactional(readOnly = true)
    public LikeStatusResponse getLikeStatus(UUID targetUserId) {
        if (targetUserId == null) {
            throw new IllegalArgumentException(
                    "El identificador del usuario es obligatorio."
            );
        }

        User currentUser = getAuthenticatedUser();

        if (currentUser.getId().equals(targetUserId)) {
            throw new IllegalArgumentException(
                    "No puedes consultar el estado de un like contigo mismo."
            );
        }

        if (!userRepository.existsById(targetUserId)) {
            throw new IllegalArgumentException(
                    "No se encontró el usuario solicitado."
            );
        }

        boolean likedByMe = likeRepository
                .existsBySender_IdAndReceiver_Id(
                        currentUser.getId(),
                        targetUserId
                );

        boolean likedMe = likeRepository
                .existsBySender_IdAndReceiver_Id(
                        targetUserId,
                        currentUser.getId()
                );

        return new LikeStatusResponse(
                targetUserId,
                likedByMe,
                likedMe,
                likedByMe && likedMe
        );
    }

    /**
     * Crea un match si no existe ya en ninguna dirección.
     */
    private void createMatchIfAbsent(User userOne, User userTwo) {
        UUID firstId = userOne.getId();
        UUID secondId = userTwo.getId();

        boolean matchExists = matchRepository
                .existsByUserOne_IdAndUserTwo_IdOrUserOne_IdAndUserTwo_Id(
                        firstId,
                        secondId,
                        secondId,
                        firstId
                );

        if (!matchExists) {
            Match match = new Match(userOne, userTwo);
            match.setStatus("ACTIVE");
            match.setLastInteractionAt(Instant.now());

            matchRepository.save(match);
        }
    }

    /**
     * Obtiene el usuario autenticado mediante su correo electrónico.
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
}