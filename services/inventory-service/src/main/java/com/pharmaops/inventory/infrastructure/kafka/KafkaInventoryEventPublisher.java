package com.pharmaops.inventory.infrastructure.kafka;

import com.pharmaops.inventory.application.port.out.InventoryEventPublisher;
import com.pharmaops.inventory.domain.model.Batch;
import com.pharmaops.inventory.domain.model.InventoryItem;
import com.pharmaops.inventory.domain.model.MovementType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaInventoryEventPublisher implements InventoryEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${pharmaops.kafka.topics.inventory-updated}")
    private String inventoryUpdatedTopic;

    @Override
    public void publishInventoryUpdated(InventoryItem item, Batch batch,
                                        int previousQuantity, MovementType movementType) {
        InventoryUpdatedPayload payload = new InventoryUpdatedPayload(
                item.getProductId().toString(),
                item.getStoreId().toString(),
                previousQuantity,
                item.getQuantity(),
                item.getMinimumQuantity(),
                batch.getId().toString(),
                batch.getExpirationDate().toString(),
                movementType.name());

        try {
            KafkaEventEnvelope envelope = new KafkaEventEnvelope(
                    UUID.randomUUID().toString(),
                    "InventoryUpdated",
                    Instant.now().toString(),
                    UUID.randomUUID().toString(),
                    "1.0",
                    payload);

            String json = objectMapper.writeValueAsString(envelope);
            kafkaTemplate.send(inventoryUpdatedTopic, item.getStoreId().toString(), json);

            log.info("Published InventoryUpdated: productId={} storeId={} previous={} current={} type={}",
                    item.getProductId(), item.getStoreId(), previousQuantity, item.getQuantity(), movementType);

        } catch (JacksonException e) {
            log.error("Failed to serialize InventoryUpdated event: {}", e.getMessage());
            throw new RuntimeException("Event serialization failed", e);
        }
    }

    record KafkaEventEnvelope(String eventId, String eventType, String timestamp,
                               String correlationId, String version, Object payload) {}

    record InventoryUpdatedPayload(String productId, String storeId,
                                   int previousQuantity, int currentQuantity,
                                   int minimumQuantity, String batchId,
                                   String batchExpiration, String movementType) {}
}
