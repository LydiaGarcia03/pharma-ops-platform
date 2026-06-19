package com.pharmaops.identity.application.port.in;

import com.pharmaops.identity.domain.model.Store;

import java.util.List;

public interface ListStoresUseCase {
    List<Store> listStores();
}
