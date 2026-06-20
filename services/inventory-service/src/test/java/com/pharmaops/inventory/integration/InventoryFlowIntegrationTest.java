package com.pharmaops.inventory.integration;

import com.pharmaops.inventory.application.port.in.AddBatchUseCase;
import com.pharmaops.inventory.application.port.in.CreateProductUseCase;
import com.pharmaops.inventory.application.port.in.ReserveStockUseCase;
import com.pharmaops.inventory.application.port.in.RestockUseCase;
import com.pharmaops.inventory.domain.exception.InsufficientStockException;
import com.pharmaops.inventory.domain.model.Batch;
import com.pharmaops.inventory.domain.model.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
class InventoryFlowIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @Autowired private CreateProductUseCase createProduct;
    @Autowired private AddBatchUseCase addBatch;
    @Autowired private RestockUseCase restock;
    @Autowired private ReserveStockUseCase reserveStock;

    // Gera um barcode único para evitar conflito de UNIQUE constraint entre testes
    private String barcode() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 13);
    }

    @Test
    void createProduct_addBatch_restock_reserve_shouldSucceedWithFEFO() {
        UUID storeId = UUID.randomUUID();

        Product product = createProduct.createProduct(
                new CreateProductUseCase.Command("Dipirona 500mg", barcode(), false, new BigDecimal("9.90")));

        Batch batch = addBatch.addBatch(
                new AddBatchUseCase.Command(product.getId(), "LOT-INT-001", LocalDate.of(2027, 12, 31), 100));

        restock.restock(
                new RestockUseCase.Command(product.getId(), storeId, batch.getId(), 50, UUID.randomUUID()));

        ReserveStockUseCase.Result result = reserveStock.reserveStock(
                new ReserveStockUseCase.Command(product.getId(), storeId, 10, UUID.randomUUID()));

        assertThat(result.remainingQuantity()).isEqualTo(40);
        assertThat(result.batch().getId()).isEqualTo(batch.getId());
    }

    @Test
    void reserveStock_withInsufficientStock_shouldThrowException() {
        UUID storeId = UUID.randomUUID();

        Product product = createProduct.createProduct(
                new CreateProductUseCase.Command("Amoxicilina 250mg", barcode(), false, new BigDecimal("25.00")));

        Batch batch = addBatch.addBatch(
                new AddBatchUseCase.Command(product.getId(), "LOT-INT-002", LocalDate.of(2027, 6, 30), 50));

        restock.restock(
                new RestockUseCase.Command(product.getId(), storeId, batch.getId(), 5, UUID.randomUUID()));

        assertThatThrownBy(() -> reserveStock.reserveStock(
                new ReserveStockUseCase.Command(product.getId(), storeId, 10, UUID.randomUUID())))
                .isInstanceOf(InsufficientStockException.class);
    }

    @Test
    void restock_multipleTimes_shouldAccumulateQuantity() {
        UUID storeId = UUID.randomUUID();

        Product product = createProduct.createProduct(
                new CreateProductUseCase.Command("Ibuprofeno 400mg", barcode(), false, new BigDecimal("18.50")));

        Batch batch = addBatch.addBatch(
                new AddBatchUseCase.Command(product.getId(), "LOT-INT-003", LocalDate.of(2028, 3, 31), 200));

        restock.restock(new RestockUseCase.Command(product.getId(), storeId, batch.getId(), 30, UUID.randomUUID()));
        restock.restock(new RestockUseCase.Command(product.getId(), storeId, batch.getId(), 20, UUID.randomUUID()));

        // 30 + 20 = 50 no estoque; reservar 1 deve deixar 49
        ReserveStockUseCase.Result result = reserveStock.reserveStock(
                new ReserveStockUseCase.Command(product.getId(), storeId, 1, UUID.randomUUID()));

        assertThat(result.remainingQuantity()).isEqualTo(49);
    }

    @Test
    void fefo_withMultipleBatches_shouldSelectEarliestExpiry() {
        UUID storeId = UUID.randomUUID();

        Product product = createProduct.createProduct(
                new CreateProductUseCase.Command("Insulina NPH", barcode(), true, new BigDecimal("89.90")));

        // Lote B vence depois, mas é cadastrado primeiro
        Batch batchB = addBatch.addBatch(
                new AddBatchUseCase.Command(product.getId(), "LOT-B", LocalDate.of(2027, 12, 1), 30));
        // Lote A vence antes — deve ser o selecionado pelo FEFO
        Batch batchA = addBatch.addBatch(
                new AddBatchUseCase.Command(product.getId(), "LOT-A", LocalDate.of(2027, 3, 1), 30));

        restock.restock(new RestockUseCase.Command(product.getId(), storeId, batchB.getId(), 20, UUID.randomUUID()));
        restock.restock(new RestockUseCase.Command(product.getId(), storeId, batchA.getId(), 20, UUID.randomUUID()));

        ReserveStockUseCase.Result result = reserveStock.reserveStock(
                new ReserveStockUseCase.Command(product.getId(), storeId, 5, UUID.randomUUID()));

        // FEFO: batchA vence 03/2027 < batchB 12/2027, então batchA deve sair primeiro
        assertThat(result.batch().getId()).isEqualTo(batchA.getId());
    }
}
