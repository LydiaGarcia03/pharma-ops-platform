package com.pharmaops.identity.application.port.in;

import com.pharmaops.identity.domain.model.Store;

public interface CreateStoreUseCase {

    Store createStore(Command command);

    record Command(String name, String taxId) {}
}
