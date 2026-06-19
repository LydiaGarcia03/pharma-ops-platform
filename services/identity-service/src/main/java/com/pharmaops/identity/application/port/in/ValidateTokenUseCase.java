package com.pharmaops.identity.application.port.in;

import java.util.List;
import java.util.UUID;

public interface ValidateTokenUseCase {

    Result validate(String token);

    record Result(UUID userId, String email, List<String> roles, UUID storeId) {}
}
