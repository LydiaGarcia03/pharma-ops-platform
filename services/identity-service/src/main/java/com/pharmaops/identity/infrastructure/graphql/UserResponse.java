package com.pharmaops.identity.infrastructure.graphql;

import com.pharmaops.identity.domain.model.User;

public record UserResponse(
        String id,
        String name,
        String email,
        boolean active,
        boolean passwordResetRequired,
        String createdAt
) {
    static UserResponse from(User user) {
        return new UserResponse(
                user.getId().toString(),
                user.getName(),
                user.getEmail(),
                user.isActive(),
                user.isPasswordResetRequired(),
                user.getCreatedAt().toString()
        );
    }
}
