
package com.soulmatch.backend.matches;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record CreateMatchRequest(

        @NotNull(message = "El identificador del primer usuario es obligatorio.")
        UUID userOneId,

        @NotNull(message = "El identificador del segundo usuario es obligatorio.")
        UUID userTwoId

) {

    public boolean hasDifferentUsers() {
        return userOneId != null
                && userTwoId != null
                && !userOneId.equals(userTwoId);
    }
}
