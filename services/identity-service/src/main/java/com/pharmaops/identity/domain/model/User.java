package com.pharmaops.identity.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class User {
    private final UUID id;
    private final String name;
    private final String email;
    private final String passwordHash;
    private final boolean active;
    private final boolean passwordResetRequired;
    private final LocalDateTime createdAt;

    public User withPasswordResetRequired(boolean value) {
        return User.builder()
                .id(this.id)
                .name(this.name)
                .email(this.email)
                .passwordHash(this.passwordHash)
                .active(this.active)
                .passwordResetRequired(value)
                .createdAt(this.createdAt)
                .build();
    }
}
