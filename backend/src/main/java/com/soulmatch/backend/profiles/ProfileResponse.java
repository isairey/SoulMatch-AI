
package com.soulmatch.backend.profiles;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record ProfileResponse(
        UUID id,
        UUID userId,
        String displayName,
        LocalDate birthDate,
        String biography,
        String gender,
        String city,
        String country,
        String occupation,
        String education,
        Instant createdAt,
        Instant updatedAt
) {

    public static ProfileResponse from(Profile profile) {
        return new ProfileResponse(
                profile.getId(),
                profile.getUser().getId(),
                profile.getDisplayName(),
                profile.getBirthDate(),
                profile.getBiography(),
                profile.getGender(),
                profile.getCity(),
                profile.getCountry(),
                profile.getOccupation(),
                profile.getEducation(),
                profile.getCreatedAt(),
                profile.getUpdatedAt()
        );
    }
}
