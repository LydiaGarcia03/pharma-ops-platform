package com.pharmaops.identity.unit.usecase;

import com.pharmaops.identity.application.port.in.ValidateTokenUseCase;
import com.pharmaops.identity.application.port.out.JwtPort;
import com.pharmaops.identity.application.usecase.ValidateTokenUseCaseImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValidateTokenUseCaseImplTest {

    @Mock
    private JwtPort jwtPort;

    @InjectMocks
    private ValidateTokenUseCaseImpl useCase;

    @Test
    void validate_shouldParseTokenAndMapClaimsToResult() {
        // given
        UUID userId = UUID.randomUUID();
        UUID storeId = UUID.randomUUID();
        var claims = new JwtPort.Claims(userId, "ana@x.com", List.of("MANAGER"), storeId);

        when(jwtPort.parseToken("some-token")).thenReturn(claims);

        // when
        ValidateTokenUseCase.Result result = useCase.validate("some-token");

        // then
        assertThat(result.userId()).isEqualTo(userId);
        assertThat(result.email()).isEqualTo("ana@x.com");
        assertThat(result.roles()).containsExactly("MANAGER");
        assertThat(result.storeId()).isEqualTo(storeId);
        verify(jwtPort).parseToken("some-token");
    }
}
