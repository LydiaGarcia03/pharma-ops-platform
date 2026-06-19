package com.pharmaops.identity.infrastructure.graphql;

import com.pharmaops.identity.application.port.in.CreateStoreUseCase;
import com.pharmaops.identity.application.port.in.ListStoresUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class StoreResolver {

    private final CreateStoreUseCase createStoreUseCase;
    private final ListStoresUseCase listStoresUseCase;

    @QueryMapping
    public List<StoreResponse> stores() {
        return listStoresUseCase.listStores().stream()
                .map(StoreResponse::from)
                .toList();
    }

    @MutationMapping
    public StoreResponse createStore(@Argument CreateStoreInput input) {
        return StoreResponse.from(createStoreUseCase.createStore(
                new CreateStoreUseCase.Command(input.name(), input.taxId())
        ));
    }

    record CreateStoreInput(String name, String taxId) {}
}
