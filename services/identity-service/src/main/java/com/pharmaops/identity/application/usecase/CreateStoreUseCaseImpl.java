package com.pharmaops.identity.application.usecase;

import com.pharmaops.identity.application.port.in.CreateStoreUseCase;
import com.pharmaops.identity.application.port.out.StoreRepository;
import com.pharmaops.identity.domain.model.Store;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
public class CreateStoreUseCaseImpl implements CreateStoreUseCase {

    private final StoreRepository storeRepository;

    @Override
    public Store createStore(Command command) {
        Store store = Store.builder()
                .id(UUID.randomUUID())
                .name(command.name())
                .taxId(command.taxId())
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();

        return storeRepository.save(store);
    }
}
