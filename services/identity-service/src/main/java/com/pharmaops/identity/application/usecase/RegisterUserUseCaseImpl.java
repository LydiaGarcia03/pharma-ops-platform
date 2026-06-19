package com.pharmaops.identity.application.usecase;

import com.pharmaops.identity.application.port.in.RegisterUserUseCase;
import com.pharmaops.identity.application.port.out.PasswordEncoder;
import com.pharmaops.identity.application.port.out.UserRepository;
import com.pharmaops.identity.domain.exception.UserAlreadyExistsException;
import com.pharmaops.identity.domain.model.User;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
public class RegisterUserUseCaseImpl implements RegisterUserUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User registerUser(Command command) {
        if (userRepository.existsByEmail(command.email())) {
            throw new UserAlreadyExistsException(command.email());
        }

        User user = User.builder()
                .id(UUID.randomUUID())
                .name(command.name())
                .email(command.email())
                .passwordHash(passwordEncoder.encode(command.password()))
                .active(true)
                .passwordResetRequired(false)
                .createdAt(LocalDateTime.now())
                .build();

        return userRepository.save(user);
    }
}
