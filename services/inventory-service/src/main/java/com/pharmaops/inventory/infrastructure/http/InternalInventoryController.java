package com.pharmaops.inventory.infrastructure.http;

import com.pharmaops.inventory.application.port.in.ReserveStockUseCase;
import com.pharmaops.inventory.domain.exception.InsufficientStockException;
import com.pharmaops.inventory.domain.exception.ProductNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/internal/inventory")
@RequiredArgsConstructor
public class InternalInventoryController {

    private final ReserveStockUseCase reserveStockUseCase;

    @PostMapping("/reserve")
    public ResponseEntity<?> reserve(@RequestBody ReserveRequest request) {
        try {
            ReserveStockUseCase.Result result = reserveStockUseCase.reserveStock(
                    new ReserveStockUseCase.Command(
                            UUID.fromString(request.productId()),
                            UUID.fromString(request.storeId()),
                            request.quantity(),
                            UUID.fromString(request.correlationId())
                    )
            );
            return ResponseEntity.ok(new ReserveResponse(
                    result.batch().getId().toString(),
                    result.remainingQuantity()
            ));
        } catch (InsufficientStockException | ProductNotFoundException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(e.getMessage()));
        }
    }

    record ReserveRequest(String productId, String storeId, int quantity, String correlationId) {}
    record ReserveResponse(String batchId, int remainingQuantity) {}
    record ErrorResponse(String message) {}
}
