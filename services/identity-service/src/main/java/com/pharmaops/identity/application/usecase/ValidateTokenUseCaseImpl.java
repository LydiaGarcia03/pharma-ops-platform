package com.pharmaops.identity.application.usecase;

import com.pharmaops.identity.application.port.in.ValidateTokenUseCase;
import com.pharmaops.identity.application.port.out.JwtPort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ValidateTokenUseCaseImpl implements ValidateTokenUseCase {

    private final JwtPort jwtPort;

    @Override
    public Result validate(String token) {
        JwtPort.Claims claims = jwtPort.parseToken(token);
        return new Result(claims.userId(), claims.email(), claims.roles(), claims.storeId());
    }
}
