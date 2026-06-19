package com.pharmaops.inventory.infrastructure.config;

import com.pharmaops.inventory.application.port.in.*;
import com.pharmaops.inventory.application.port.out.*;
import com.pharmaops.inventory.application.usecase.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfiguration {

    @Bean
    public CreateProductUseCase createProductUseCase(ProductRepository productRepository) {
        return new CreateProductUseCaseImpl(productRepository);
    }

    @Bean
    public AddBatchUseCase addBatchUseCase(ProductRepository productRepository,
                                           BatchRepository batchRepository,
                                           InventoryRepository inventoryRepository) {
        return new AddBatchUseCaseImpl(productRepository, batchRepository, inventoryRepository);
    }

    @Bean
    public GetInventoryUseCase getInventoryUseCase(InventoryRepository inventoryRepository) {
        return new GetInventoryUseCaseImpl(inventoryRepository);
    }

    @Bean
    public ReserveStockUseCase reserveStockUseCase(InventoryRepository inventoryRepository,
                                                   BatchRepository batchRepository,
                                                   InventoryMovementRepository movementRepository,
                                                   InventoryEventPublisher eventPublisher) {
        return new ReserveStockUseCaseImpl(inventoryRepository, batchRepository, movementRepository, eventPublisher);
    }

    @Bean
    public RestockUseCase restockUseCase(BatchRepository batchRepository,
                                         InventoryRepository inventoryRepository,
                                         InventoryMovementRepository movementRepository) {
        return new RestockUseCaseImpl(batchRepository, inventoryRepository, movementRepository);
    }

    @Bean
    public ProcessReturnUseCase processReturnUseCase(BatchRepository batchRepository,
                                                     InventoryRepository inventoryRepository,
                                                     InventoryMovementRepository movementRepository) {
        return new ProcessReturnUseCaseImpl(batchRepository, inventoryRepository, movementRepository);
    }
}
