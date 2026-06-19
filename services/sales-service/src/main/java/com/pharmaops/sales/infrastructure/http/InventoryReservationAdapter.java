package com.pharmaops.sales.infrastructure.http;

import com.pharmaops.sales.application.port.out.InventoryReservationPort;
import com.pharmaops.sales.domain.exception.StockReservationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.UUID;

@Slf4j
@Component
public class InventoryReservationAdapter implements InventoryReservationPort {

    private final RestClient restClient;

    // RestClient é o cliente HTTP síncrono moderno do Spring (substituiu RestTemplate).
    // Criado com baseUrl para não repetir o endereço em cada chamada.
    public InventoryReservationAdapter(
            @Value("${pharmaops.services.inventory.url:http://localhost:8082}") String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    @Override
    public Result reserve(UUID productId, UUID storeId, int quantity, UUID correlationId) {
        var requestBody = new ReserveRequest(
                productId.toString(), storeId.toString(), quantity, correlationId.toString());

        try {
            var response = restClient.post()
                    .uri("/internal/inventory/reserve")
                    .body(requestBody)
                    .retrieve()
                    .onStatus(status -> status == HttpStatus.CONFLICT, (req, res) -> {
                        throw new StockReservationException("insufficient stock for product " + productId);
                    })
                    .body(ReserveResponse.class);

            if (response == null) {
                throw new StockReservationException("empty response from inventory-service");
            }

            return new Result(UUID.fromString(response.batchId()), response.remainingQuantity());

        } catch (StockReservationException e) {
            throw e;
        } catch (RestClientException e) {
            log.error("Failed to reach inventory-service for product {}: {}", productId, e.getMessage());
            throw new StockReservationException("inventory-service unavailable");
        }
    }

    record ReserveRequest(String productId, String storeId, int quantity, String correlationId) {}
    record ReserveResponse(String batchId, int remainingQuantity) {}
}
