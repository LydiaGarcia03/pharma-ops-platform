package com.pharmaops.identity.application.usecase;

import com.pharmaops.identity.application.port.in.ListStoresUseCase;
import com.pharmaops.identity.application.port.out.StoreRepository;
import com.pharmaops.identity.domain.model.Store;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ListStoresUseCaseImpl implements ListStoresUseCase {

    private final StoreRepository storeRepository;

    @Override
    public List<Store> listStores() {
        return storeRepository.findAll();
    }
}
