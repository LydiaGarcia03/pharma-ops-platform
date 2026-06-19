package com.pharmaops.inventory.infrastructure.kafka;

import com.pharmaops.inventory.application.port.out.InventoryEventPublisher;
import com.pharmaops.inventory.domain.model.Batch;
import com.pharmaops.inventory.domain.model.InventoryItem;
import com.pharmaops.inventory.domain.model.MovementType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

// Implementação temporária — substituída pelo KafkaInventoryEventPublisher na Fase 3.
@Slf4j
@Component
public class NoOpInventoryEventPublisher implements InventoryEventPublisher {

    @Override
    public void publishInventoryUpdated(InventoryItem item, Batch batch,
                                        int previousQuantity, MovementType movementType) {
        log.info("[NO-OP] InventoryUpdated event skipped — Kafka not yet configured. " +
                "product={} store={} previous={} current={} type={}",
                item.getProductId(), item.getStoreId(),
                previousQuantity, item.getQuantity(), movementType);
    }
}
