package com.pharmaops.identity.unit.usecase;

import com.pharmaops.identity.application.port.in.RegisterUserUseCase;
import com.pharmaops.identity.application.port.out.PasswordEncoder;
import com.pharmaops.identity.application.port.out.UserRepository;
import com.pharmaops.identity.application.usecase.RegisterUserUseCaseImpl;
import com.pharmaops.identity.domain.exception.UserAlreadyExistsException;
import com.pharmaops.identity.domain.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterUserUseCaseImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private RegisterUserUseCaseImpl useCase;

    @Test
    void registerUser_whenEmailNotExists_shouldEncodePasswordAndSaveUser() {
        // given
        var command = new RegisterUserUseCase.Command("Ana", "ana@x.com", "password");
        var savedUser = buildUser("ana@x.com", "hashed-password");

        when(userRepository.existsByEmail("ana@x.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("hashed-password");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // when
        User result = useCase.registerUser(command);

        // then
        assertThat(result.getEmail()).isEqualTo("ana@x.com");
        assertThat(result.getPasswordHash()).isEqualTo("hashed-password");
        assertThat(result.isPasswordResetRequired()).isFalse();
        verify(passwordEncoder).encode("password");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUser_whenEmailAlreadyExists_shouldThrowUserAlreadyExistsException() {
        // given
        var command = new RegisterUserUseCase.Command("Ana", "ana@x.com", "password");
        when(userRepository.existsByEmail("ana@x.com")).thenReturn(true);

        // when / then
        assertThrows(UserAlreadyExistsException.class, () -> useCase.registerUser(command));
        verify(userRepository, never()).save(any());
    }

    private User buildUser(String email, String passwordHash) {
        return User.builder()
                .id(UUID.randomUUID())
                .name("Ana")
                .email(email)
                .passwordHash(passwordHash)
                .active(true)
                .passwordResetRequired(false)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
