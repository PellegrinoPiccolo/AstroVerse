package com.astroverse.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "user_space")
public class UserSpace {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;
    @ManyToOne
    @JoinColumn(name = "space_id", nullable = false)
    @JsonIgnore
    private Space space;
    private boolean isSpaceAdmin;
    private LocalDateTime createdAt;
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public UserSpace(User user, Space space) {
        this.user = user;
        this.space = space;
    }

    @JsonProperty("userId")
    public long getUserId() {
        return user != null ? user.getId() : 0;
    }

    @JsonProperty("spaceId")
    public long getSpaceId() {
        return space != null ? space.getId() : 0;
    }
}