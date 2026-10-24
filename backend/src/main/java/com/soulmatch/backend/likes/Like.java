
package com.soulmatch.backend.likes;

import com.soulmatch.backend.users.User;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
    name = "likes",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_likes_from_to",
            columnNames = {"sender_id", "receiver_id"}
        )
    },
    indexes = {
        @Index(name = "idx_likes_sender", columnList = "sender_id"),
        @Index(name = "idx_likes_receiver", columnList = "receiver_id"),
        @Index(name = "idx_likes_created_at", columnList = "created_at")
    }
)
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public Like() {
    }

    public Like(User sender, User receiver) {
        this.sender = sender;
        this.receiver = receiver;
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
