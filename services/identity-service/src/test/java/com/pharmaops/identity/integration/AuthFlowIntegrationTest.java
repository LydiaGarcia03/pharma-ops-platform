package com.pharmaops.identity.integration;

import com.pharmaops.identity.application.port.in.AuthenticateUserUseCase;
import com.pharmaops.identity.application.port.in.CreateStoreUseCase;
import com.pharmaops.identity.application.port.in.ListStoresUseCase;
import com.pharmaops.identity.application.port.in.RegisterUserUseCase;
import com.pharmaops.identity.domain.exception.InvalidCredentialsException;
import com.pharmaops.identity.domain.model.Store;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
class AuthFlowIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    // Redis não tem @ServiceConnection padrão para GenericContainer, então usamos @DynamicPropertySource
    @Container
    @SuppressWarnings("resource")
    static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7"))
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void configureRedis(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
    }

    @Autowired private RegisterUserUseCase registerUser;
    @Autowired private AuthenticateUserUseCase authenticateUser;
    @Autowired private CreateStoreUseCase createStore;
    @Autowired private ListStoresUseCase listStores;

    @Test
    void registerUser_thenAuthenticate_shouldReturnValidTokens() {
        String email = "user_" + UUID.randomUUID() + "@test.com";

        registerUser.registerUser(new RegisterUserUseCase.Command("Test User", email, "StrongPass@123"));

        AuthenticateUserUseCase.Result result = authenticateUser.authenticate(
                new AuthenticateUserUseCase.Command(email, "StrongPass@123"));

        assertThat(result.accessToken()).isNotBlank();
        assertThat(result.refreshToken()).isNotBlank();
        assertThat(result.userEmail()).isEqualTo(email);
        assertThat(result.passwordResetRequired()).isFalse();
    }

    @Test
    void authenticate_withWrongPassword_shouldThrowInvalidCredentials() {
        String email = "user_" + UUID.randomUUID() + "@test.com";
        registerUser.registerUser(new RegisterUserUseCase.Command("Test User", email, "CorrectPass@123"));

        assertThatThrownBy(() -> authenticateUser.authenticate(
                new AuthenticateUserUseCase.Command(email, "WrongPass@999")))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void authenticate_withUnknownEmail_shouldThrowInvalidCredentials() {
        assertThatThrownBy(() -> authenticateUser.authenticate(
                new AuthenticateUserUseCase.Command("nobody@test.com", "AnyPass@123")))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void createStore_shouldPersistAndBeListable() {
        String storeName = "Integration Store " + UUID.randomUUID();
        String taxId = UUID.randomUUID().toString().replace("-", "").substring(0, 14);

        Store created = createStore.createStore(new CreateStoreUseCase.Command(storeName, taxId));

        List<Store> stores = listStores.listStores();

        assertThat(created.getId()).isNotNull();
        assertThat(created.getName()).isEqualTo(storeName);
        assertThat(stores).anyMatch(s -> s.getId().equals(created.getId()));
    }
}
