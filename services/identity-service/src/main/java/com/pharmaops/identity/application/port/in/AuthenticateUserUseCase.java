package com.pharmaops.identity.application.port.in;

public interface AuthenticateUserUseCase {

    Result authenticate(Command command);

    record Command(String email, String password) {}

    record Result(String accessToken, String refreshToken, boolean passwordResetRequired) {}
}
