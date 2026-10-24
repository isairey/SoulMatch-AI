
package com.soulmatch.backend.likes;

import java.util.UUID;

/**
 * DTO que representa el estado de un like entre dos usuarios.
 *
 * @param targetUserId ID del usuario consultado.
 * @param likedByMe    Indica si el usuario autenticado le dio like.
 * @param likedMe      Indica si el otro usuario le dio like al usuario autenticado.
 * @param mutualLike   Indica si existe un like mutuo.
 */
public record LikeStatusResponse(
        UUID targetUserId,
        boolean likedByMe,
        boolean likedMe,
        boolean mutualLike
) {
}
