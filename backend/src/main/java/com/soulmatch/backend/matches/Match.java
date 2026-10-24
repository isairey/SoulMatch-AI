
package com.soulmatch.backend.matches;

import com.soulmatch.backend.users.User;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
    name = "user_matches",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_user_matches_users",
            columnNames = {"user_one_id", "user_two_id"}
        )
    },
    indexes = {
        @Index(name = "idx_user_matches_user_one", columnList = "user_one_id"),
        @Index(name = "idx_user_matches_user_two", columnList = "user_two_id"),
        @Index(name = "idx_user_matches_status", columnList = "status")
    }
)
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_one_id", nullable = false)
    private User userOne;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_two_id", nullable = false)
    private User userTwo;

    @Column(name = "status", nullable = false, length = 30)
    private String status = "ACTIVE";

    @Column(name = "matched_at", nullable = false, updatable = false)
    private Instant matchedAt;

    @Column(name = "last_interaction_at")
    private Instant lastInteractionAt;

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();

        if (matchedAt == null) {
            matchedAt = now;
        }

        if (lastInteractionAt == null) {
            lastInteractionAt = now;
        }

        if (status == null || status.isBlank()) {
            status = "ACTIVE";
        }
    }

    public Match() {
    }

    public Match(User userOne, User userTwo) {
        this.userOne = userOne;
        this.userTwo = userTwo;
        this.status = "ACTIVE";
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public User getUserOne() {
        return userOne;
    }

    public void setUserOne(User userOne) {
        this.userOne = userOne;
    }

    public User getUserTwo() {
        return userTwo;
    }

    public void setUserTwo(User userTwo) {
        this.userTwo = userTwo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getMatchedAt() {
        return matchedAt;
    }

    public void setMatchedAt(Instant matchedAt) {
        this.matchedAt = matchedAt;
    }

    public Instant getLastInteractionAt() {
        return lastInteractionAt;
    }

    public void setLastInteractionAt(Instant lastInteractionAt) {
        this.lastInteractionAt = lastInteractionAt;
    }
}
