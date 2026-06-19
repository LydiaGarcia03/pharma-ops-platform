package com.pharmaops.identity.infrastructure.graphql;

import com.pharmaops.identity.application.port.in.AuthenticateUserUseCase;
import com.pharmaops.identity.application.port.in.RegisterUserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class AuthResolver {

    private final AuthenticateUserUseCase authenticateUserUseCase;
    private final RegisterUserUseCase registerUserUseCase;

    @MutationMapping
    public AuthPayload login(@Argument LoginInput input) {
        AuthenticateUserUseCase.Result result = authenticateUserUseCase.authenticate(
                new AuthenticateUserUseCase.Command(input.email(), input.password())
        );
        UserResponse user = new UserResponse(result.userId(), result.userName(), result.userEmail(), true, result.passwordResetRequired(), null);
        return new AuthPayload(result.accessToken(), result.refreshToken(), result.passwordResetRequired(), user);
    }

    @MutationMapping
    public UserResponse register(@Argument RegisterUserInput input) {
        return UserResponse.from(registerUserUseCase.registerUser(
                new RegisterUserUseCase.Command(input.name(), input.email(), input.password())
        ));
    }

    record LoginInput(String email, String password) {}

    record RegisterUserInput(String name, String email, String password) {}

    record AuthPayload(String accessToken, String refreshToken, boolean passwordResetRequired, UserResponse user) {}
}
