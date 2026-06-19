package com.pharmaops.inventory.infrastructure.graphql;

import com.pharmaops.inventory.application.port.in.GetInventoryUseCase;
import com.pharmaops.inventory.application.port.in.RestockUseCase;
import com.pharmaops.inventory.domain.model.InventoryItem;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class InventoryResolver {

    private final GetInventoryUseCase getInventoryUseCase;
    private final RestockUseCase restockUseCase;

    @QueryMapping
    public InventoryItemResponse inventory(@Argument String productId, @Argument String storeId) {
        InventoryItem item = getInventoryUseCase.getInventory(
                UUID.fromString(productId), UUID.fromString(storeId));
        return InventoryItemResponse.from(item);
    }

    @MutationMapping
    public boolean restock(@Argument RestockInput input) {
        restockUseCase.restock(new RestockUseCase.Command(
                UUID.fromString(input.productId()),
                UUID.fromString(input.storeId()),
                UUID.fromString(input.batchId()),
                input.quantity(),
                UUID.fromString(input.correlationId())
        ));
        return true;
    }

    record RestockInput(String productId, String storeId, String batchId, int quantity, String correlationId) {}
}
