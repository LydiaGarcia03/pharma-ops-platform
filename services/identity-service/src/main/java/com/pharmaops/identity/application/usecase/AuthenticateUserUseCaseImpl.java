package com.pharmaops.identity.application.usecase;

import com.pharmaops.identity.application.port.in.AuthenticateUserUseCase;
import com.pharmaops.identity.application.port.out.JwtPort;
import com.pharmaops.identity.application.port.out.PasswordEncoder;
import com.pharmaops.identity.application.port.out.TokenRepository;
import com.pharmaops.identity.application.port.out.UserRepository;
import com.pharmaops.identity.domain.exception.InvalidCredentialsException;
import com.pharmaops.identity.domain.model.User;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class AuthenticateUserUseCaseImpl implements AuthenticateUserUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtPort jwtPort;
    private final TokenRepository tokenRepository;
    private final Duration refreshTokenTtl;

    @Override
    public Result authenticate(Command command) {
        User user = userRepository.findByEmail(command.email())
                .orElseThrow(InvalidCredentialsException::new);

        if (!user.isActive()) {
            throw new InvalidCredentialsException();
        }

        if (!passwordEncoder.matches(command.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        // storeId is null here: the access token carries the store context per request,
        // not at login time. The GraphQL resolver will pass it via header.
        String accessToken = jwtPort.generateAccessToken(
                user.getId(), user.getEmail(), List.of(), null
        );

        String refreshToken = UUID.randomUUID().toString();
        tokenRepository.save(user.getId(), refreshToken, refreshTokenTtl);

        return new Result(accessToken, refreshToken, user.isPasswordResetRequired(),
                user.getId().toString(), user.getName(), user.getEmail());
    }
}
