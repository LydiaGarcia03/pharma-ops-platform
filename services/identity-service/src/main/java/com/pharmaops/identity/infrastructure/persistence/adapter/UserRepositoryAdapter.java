package com.pharmaops.identity.infrastructure.persistence.adapter;

import com.pharmaops.identity.application.port.out.UserRepository;
import com.pharmaops.identity.domain.model.User;
import com.pharmaops.identity.infrastructure.persistence.entity.UserEntity;
import com.pharmaops.identity.infrastructure.persistence.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {

    private final UserJpaRepository jpaRepository;

    @Override
    public Optional<User> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaRepository.findByEmail(email).map(this::toDomain);
    }

    @Override
    public User save(User user) {
        return toDomain(jpaRepository.save(toEntity(user)));
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    @Override
    public boolean existsUserWithProfile(String profileName) {
        return jpaRepository.countByProfileName(profileName) > 0;
    }

    private User toDomain(UserEntity e) {
        return User.builder()
                .id(e.getId())
                .name(e.getName())
                .email(e.getEmail())
                .passwordHash(e.getPasswordHash())
                .active(e.isActive())
                .passwordResetRequired(e.isPasswordResetRequired())
                .createdAt(e.getCreatedAt())
                .build();
    }

    private UserEntity toEntity(User u) {
        return UserEntity.builder()
                .id(u.getId())
                .name(u.getName())
                .email(u.getEmail())
                .passwordHash(u.getPasswordHash())
                .active(u.isActive())
                .passwordResetRequired(u.isPasswordResetRequired())
                .createdAt(u.getCreatedAt())
                .build();
    }
}
