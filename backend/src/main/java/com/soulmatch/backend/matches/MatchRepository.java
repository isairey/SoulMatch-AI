
package com.soulmatch.backend.matches;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MatchRepository extends JpaRepository<Match, UUID> {

    /**
     * Obtiene los matches activos de un usuario,
     * ya sea como primer o segundo participante.
     */
    List<Match> findAllByUserOne_IdOrUserTwo_IdOrderByMatchedAtDesc(
            UUID userOneId,
            UUID userTwoId
    );

    /**
     * Obtiene los matches activos de un usuario.
     */
    List<Match> findAllByStatusAndUserOne_IdOrStatusAndUserTwo_IdOrderByMatchedAtDesc(
            String statusOne,
            UUID userOneId,
            String statusTwo,
            UUID userTwoId
    );

    /**
     * Busca un match entre dos usuarios en cualquier orden.
     */
    Optional<Match> findByUserOne_IdAndUserTwo_IdOrUserOne_IdAndUserTwo_Id(
            UUID userOneId,
            UUID userTwoId,
            UUID userOneReverseId,
            UUID userTwoReverseId
    );

    /**
     * Comprueba si existe un match entre dos usuarios
     * en cualquier orden.
     */
    boolean existsByUserOne_IdAndUserTwo_IdOrUserOne_IdAndUserTwo_Id(
            UUID userOneId,
            UUID userTwoId,
            UUID userOneReverseId,
            UUID userTwoReverseId
    );

    /**
     * Cuenta los matches de un usuario.
     */
    long countByUserOne_IdOrUserTwo_Id(
            UUID userOneId,
            UUID userTwoId
    );

    /**
     * Busca los matches por estado.
     */
    List<Match> findAllByStatusOrderByMatchedAtDesc(String status);
}
