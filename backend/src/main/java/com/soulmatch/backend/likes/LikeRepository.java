
package com.soulmatch.backend.likes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LikeRepository extends JpaRepository<Like, UUID> {

    /**
     * Busca si un usuario ya dio like a otro.
     */
    Optional<Like> findBySender_IdAndReceiver_Id(
            UUID senderId,
            UUID receiverId
    );

    /**
     * Comprueba si existe un like entre dos usuarios en una dirección.
     */
    boolean existsBySender_IdAndReceiver_Id(
            UUID senderId,
            UUID receiverId
    );

    /**
     * Obtiene los likes enviados por un usuario.
     */
    List<Like> findAllBySender_IdOrderByCreatedAtDesc(UUID senderId);

    /**
     * Obtiene los likes recibidos por un usuario.
     */
    List<Like> findAllByReceiver_IdOrderByCreatedAtDesc(UUID receiverId);

    /**
     * Elimina un like entre dos usuarios.
     */
    long deleteBySender_IdAndReceiver_Id(
            UUID senderId,
            UUID receiverId
    );

    /**
     * Cuenta los likes recibidos por un usuario.
     */
    long countByReceiver_Id(UUID receiverId);

    /**
     * Cuenta los likes enviados por un usuario.
     */
    long countBySender_Id(UUID senderId);

    /**
     * Comprueba si existe un like mutuo.
     */
    default boolean existsMutualLike(UUID userOneId, UUID userTwoId) {
        return existsBySender_IdAndReceiver_Id(userOneId, userTwoId)
                && existsBySender_IdAndReceiver_Id(userTwoId, userOneId);
    }
}
