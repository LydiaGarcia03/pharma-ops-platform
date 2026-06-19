package com.pharmaops.identity.unit.usecase;

import com.pharmaops.identity.application.port.in.AuthenticateUserUseCase;
import com.pharmaops.identity.application.port.out.JwtPort;
import com.pharmaops.identity.application.port.out.PasswordEncoder;
import com.pharmaops.identity.application.port.out.TokenRepository;
import com.pharmaops.identity.application.port.out.UserRepository;
import com.pharmaops.identity.application.usecase.AuthenticateUserUseCaseImpl;
import com.pharmaops.identity.domain.exception.InvalidCredentialsException;
import com.pharmaops.identity.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticateUserUseCaseImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtPort jwtPort;

    @Mock
    private TokenRepository tokenRepository;

    // @InjectMocks não funciona aqui porque Duration não é um mock.
    // Construímos manualmente com o TTL desejado.
    private AuthenticateUserUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new AuthenticateUserUseCaseImpl(
                userRepository, passwordEncoder, jwtPort, tokenRepository, Duration.ofDays(7)
        );
    }

    @Test
    void authenticate_whenValidCredentials_shouldReturnAccessAndRefreshTokens() {
        // given
        var command = new AuthenticateUserUseCase.Command("ana@x.com", "password");
        User user = buildActiveUser("ana@x.com", "hashed");

        when(userRepository.findByEmail("ana@x.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "hashed")).thenReturn(true);
        when(jwtPort.generateAccessToken(any(), any(), any(), any())).thenReturn("access-token");

        // when
        AuthenticateUserUseCase.Result result = useCase.authenticate(command);

        // then
        assertThat(result.accessToken()).isEqualTo("access-token");
        assertThat(result.refreshToken()).isNotBlank();
        assertThat(result.passwordResetRequired()).isFalse();
        verify(tokenRepository).save(eq(user.getId()), anyString(), eq(Duration.ofDays(7)));
    }

    @Test
    void authenticate_whenPasswordResetRequired_shouldReturnFlagTrue() {
        // given
        User user = User.builder()
                .id(UUID.randomUUID()).name("Ana").email("ana@x.com")
                .passwordHash("hashed").active(true).passwordResetRequired(true)
                .createdAt(LocalDateTime.now()).build();

        when(userRepository.findByEmail("ana@x.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        when(jwtPort.generateAccessToken(any(), any(), any(), any())).thenReturn("token");

        // when
        var result = useCase.authenticate(new AuthenticateUserUseCase.Command("ana@x.com", "password"));

        // then
        assertThat(result.passwordResetRequired()).isTrue();
    }

    @Test
    void authenticate_whenUserNotFound_shouldThrowInvalidCredentialsException() {
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class,
                () -> useCase.authenticate(new AuthenticateUserUseCase.Command("x@x.com", "pass")));

        verify(passwordEncoder, never()).matches(any(), any());
    }

    @Test
    void authenticate_whenUserInactive_shouldThrowInvalidCredentialsException() {
        User inactive = User.builder()
                .id(UUID.randomUUID()).name("Ana").email("ana@x.com")
                .passwordHash("hashed").active(false).passwordResetRequired(false)
                .createdAt(LocalDateTime.now()).build();

        when(userRepository.findByEmail(any())).thenReturn(Optional.of(inactive));

        assertThrows(InvalidCredentialsException.class,
                () -> useCase.authenticate(new AuthenticateUserUseCase.Command("ana@x.com", "pass")));

        verify(passwordEncoder, never()).matches(any(), any());
    }

    @Test
    void authenticate_whenWrongPassword_shouldThrowInvalidCredentialsException() {
        User user = buildActiveUser("ana@x.com", "hashed");
        when(userRepository.findByEmail("ana@x.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "hashed")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class,
                () -> useCase.authenticate(new AuthenticateUserUseCase.Command("ana@x.com", "wrong")));

        verify(jwtPort, never()).generateAccessToken(any(), any(), any(), any());
    }

    private User buildActiveUser(String email, String passwordHash) {
        return User.builder()
                .id(UUID.randomUUID()).name("Ana").email(email)
                .passwordHash(passwordHash).active(true).passwordResetRequired(false)
                .createdAt(LocalDateTime.now()).build();
    }
}
