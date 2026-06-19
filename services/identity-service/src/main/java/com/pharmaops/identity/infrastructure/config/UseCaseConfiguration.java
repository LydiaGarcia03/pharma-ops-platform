package com.pharmaops.identity.infrastructure.config;

import com.pharmaops.identity.application.port.in.*;
import com.pharmaops.identity.application.port.out.*;
import com.pharmaops.identity.application.usecase.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class UseCaseConfiguration {

    @Bean
    public RegisterUserUseCase registerUserUseCase(UserRepository userRepository,
                                                   PasswordEncoder passwordEncoder) {
        return new RegisterUserUseCaseImpl(userRepository, passwordEncoder);
    }

    @Bean
    public AuthenticateUserUseCase authenticateUserUseCase(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtPort jwtPort,
            TokenRepository tokenRepository,
            @Value("${jwt.refresh-expiration-days:7}") long refreshExpirationDays) {
        return new AuthenticateUserUseCaseImpl(
                userRepository, passwordEncoder, jwtPort, tokenRepository,
                Duration.ofDays(refreshExpirationDays));
    }

    @Bean
    public CreateStoreUseCase createStoreUseCase(StoreRepository storeRepository) {
        return new CreateStoreUseCaseImpl(storeRepository);
    }

    @Bean
    public ListStoresUseCase listStoresUseCase(StoreRepository storeRepository) {
        return new ListStoresUseCaseImpl(storeRepository);
    }

    @Bean
    public AssignProfileUseCase assignProfileUseCase(UserRepository userRepository,
                                                     ProfileRepository profileRepository,
                                                     StoreRepository storeRepository) {
        return new AssignProfileUseCaseImpl(userRepository, profileRepository, storeRepository);
    }

    @Bean
    public ValidateTokenUseCase validateTokenUseCase(JwtPort jwtPort) {
        return new ValidateTokenUseCaseImpl(jwtPort);
    }
}
