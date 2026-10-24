
package com.soulmatch.backend.likes;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/likes")
public class LikeController {

    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    /**
     * Da like a otro usuario.
     */
    @PostMapping("/{targetUserId}")
    @ResponseStatus(HttpStatus.CREATED)
    public LikeResponse createLike(
            @PathVariable UUID targetUserId
    ) {
        return likeService.createLike(targetUserId);
    }

    /**
     * Obtiene los likes recibidos por el usuario autenticado.
     */
    @GetMapping("/received")
    public List<LikeResponse> getReceivedLikes() {
        return likeService.getReceivedLikes();
    }

    /**
     * Obtiene los likes enviados por el usuario autenticado.
     */
    @GetMapping("/sent")
    public List<LikeResponse> getSentLikes() {
        return likeService.getSentLikes();
    }

    /**
     * Retira el like enviado a un usuario.
     */
    @DeleteMapping("/{targetUserId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeLike(
            @PathVariable UUID targetUserId
    ) {
        likeService.removeLike(targetUserId);
    }

    /**
     * Comprueba si existe un like entre el usuario autenticado
     * y otro usuario.
     */
    @GetMapping("/status/{targetUserId}")
    public LikeStatusResponse getLikeStatus(
            @PathVariable UUID targetUserId
    ) {
        return likeService.getLikeStatus(targetUserId);
    }
}
