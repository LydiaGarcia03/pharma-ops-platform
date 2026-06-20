package com.pharmaops.sales.integration;

import com.pharmaops.sales.application.port.in.CreateSaleUseCase;
import com.pharmaops.sales.application.port.in.GetSaleUseCase;
import com.pharmaops.sales.application.port.in.ProcessReturnUseCase;
import com.pharmaops.sales.application.port.out.InventoryReservationPort;
import com.pharmaops.sales.application.port.out.SaleEventPublisher;
import com.pharmaops.sales.domain.exception.StockReservationException;
import com.pharmaops.sales.domain.model.Return;
import com.pharmaops.sales.domain.model.Sale;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
class SaleFlowIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    // @MockitoBean é o substituto do @MockBean (removido no Spring Boot 4).
    // Substitui o InventoryReservationAdapter no contexto Spring pelo mock,
    // sem precisar de WireMock — incompatível com Java 25.
    @MockitoBean
    InventoryReservationPort inventoryReservationPort;

    // Evita que o KafkaSaleEventPublisher tente conectar ao Kafka durante os testes
    @MockitoBean
    SaleEventPublisher saleEventPublisher;

    @Autowired private CreateSaleUseCase createSale;
    @Autowired private GetSaleUseCase getSale;
    @Autowired private ProcessReturnUseCase processReturn;

    @Test
    void createSale_whenInventoryReservesSuccessfully_shouldPersistConfirmedSale() {
        UUID batchId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        when(inventoryReservationPort.reserve(any(), any(), anyInt(), any()))
                .thenReturn(new InventoryReservationPort.Result(batchId, 40));

        Sale sale = createSale.createSale(new CreateSaleUseCase.Command(
                UUID.randomUUID(), UUID.randomUUID(), null, false,
                List.of(new CreateSaleUseCase.ItemCommand(productId, false, 2, new BigDecimal("29.90")))));

        assertThat(sale.getId()).isNotNull();
        assertThat(sale.getStatus().name()).isEqualTo("CONFIRMED");
        assertThat(sale.getTotal()).isEqualByComparingTo(new BigDecimal("59.80"));
        assertThat(sale.getItems()).hasSize(1);
        assertThat(sale.getItems().get(0).getBatchId()).isEqualTo(batchId);
    }

    @Test
    void createSale_whenInventoryThrowsStockException_shouldPropagateException() {
        when(inventoryReservationPort.reserve(any(), any(), anyInt(), any()))
                .thenThrow(new StockReservationException("insufficient stock"));

        assertThatThrownBy(() -> createSale.createSale(new CreateSaleUseCase.Command(
                UUID.randomUUID(), UUID.randomUUID(), null, false,
                List.of(new CreateSaleUseCase.ItemCommand(
                        UUID.randomUUID(), false, 10, new BigDecimal("15.00"))))))
                .isInstanceOf(StockReservationException.class);
    }

    @Test
    void getSale_afterCreate_shouldReturnPersistedSale() {
        UUID batchId = UUID.randomUUID();

        when(inventoryReservationPort.reserve(any(), any(), anyInt(), any()))
                .thenReturn(new InventoryReservationPort.Result(batchId, 10));

        Sale created = createSale.createSale(new CreateSaleUseCase.Command(
                UUID.randomUUID(), UUID.randomUUID(), null, false,
                List.of(new CreateSaleUseCase.ItemCommand(
                        UUID.randomUUID(), false, 1, new BigDecimal("50.00")))));

        Sale fetched = getSale.getSale(created.getId());

        assertThat(fetched.getId()).isEqualTo(created.getId());
        assertThat(fetched.getTotal()).isEqualByComparingTo(created.getTotal());
    }

    @Test
    void processReturn_shouldPersistReturnWithCorrectData() {
        UUID batchId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        when(inventoryReservationPort.reserve(any(), any(), anyInt(), any()))
                .thenReturn(new InventoryReservationPort.Result(batchId, 40));

        Sale sale = createSale.createSale(new CreateSaleUseCase.Command(
                UUID.randomUUID(), userId, null, false,
                List.of(new CreateSaleUseCase.ItemCommand(productId, false, 3, new BigDecimal("10.00")))));

        Return returnRecord = processReturn.processReturn(new ProcessReturnUseCase.Command(
                sale.getId(), productId, batchId, userId, null, 1, "Damaged packaging"));

        assertThat(returnRecord.getId()).isNotNull();
        assertThat(returnRecord.getSaleId()).isEqualTo(sale.getId());
        assertThat(returnRecord.getProductId()).isEqualTo(productId);
        assertThat(returnRecord.getQuantity()).isEqualTo(1);
        assertThat(returnRecord.getReason()).isEqualTo("Damaged packaging");
    }
}
