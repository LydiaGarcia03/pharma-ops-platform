package com.pharmaops.identity.application.port.in;

import java.util.UUID;

public interface AssignProfileUseCase {

    void assignProfile(Command command);

    // storeId null = ADMIN de rede (acesso global)
    record Command(UUID userId, UUID profileId, UUID storeId) {}
}
