
package com.soulmatch.backend.interests;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import com.soulmatch.backend.users.User;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(
    name = "user_interests",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_user_interests_user_interest",
            columnNames = {"user_id", "interest_id"}
        )
    }
)
public class UserInterest {

    @EmbeddedId
    private UserInterestId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("interestId")
    @JoinColumn(name = "interest_id", nullable = false)
    private Interest interest;

    public UserInterest() {
    }

    public UserInterest(User user, Interest interest) {
        this.user = Objects.requireNonNull(
            user, "El usuario es obligatorio"
        );
        this.interest = Objects.requireNonNull(
            interest, "El interés es obligatorio"
        );

        this.id = new UserInterestId(
            user.getId(),
            interest.getId()
        );
    }

    public UserInterestId getId() {
        return id;
    }

    public void setId(UserInterestId id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        updateId();
    }

    public Interest getInterest() {
        return interest;
    }

    public void setInterest(Interest interest) {
        this.interest = interest;
        updateId();
    }

    private void updateId() {
        if (user != null
                && user.getId() != null
                && interest != null
                && interest.getId() != null) {
            this.id = new UserInterestId(
                user.getId(),
                interest.getId()
            );
        }
    }

    /**
     * Clave compuesta de la relación usuario-interés.
     */
    public static class UserInterestId implements Serializable {

        private UUID userId;
        private UUID interestId;

        public UserInterestId() {
        }

        public UserInterestId(UUID userId, UUID interestId) {
            this.userId = userId;
            this.interestId = interestId;
        }

        public UUID getUserId() {
            return userId;
        }

        public void setUserId(UUID userId) {
            this.userId = userId;
        }

        public UUID getInterestId() {
            return interestId;
        }

        public void setInterestId(UUID interestId) {
            this.interestId = interestId;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }

            if (!(object instanceof UserInterestId other)) {
                return false;
            }

            return Objects.equals(userId, other.userId)
                && Objects.equals(interestId, other.interestId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(userId, interestId);
        }
    }
}
