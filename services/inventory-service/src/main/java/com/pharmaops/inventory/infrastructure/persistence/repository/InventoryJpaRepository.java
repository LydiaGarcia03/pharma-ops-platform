package com.pharmaops.inventory.infrastructure.persistence.repository;

import com.pharmaops.inventory.infrastructure.persistence.entity.InventoryEntity;
import com.pharmaops.inventory.infrastructure.persistence.entity.InventoryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface InventoryJpaRepository extends JpaRepository<InventoryEntity, InventoryId> {

    Optional<InventoryEntity> findById_ProductIdAndId_StoreId(UUID productId, UUID storeId);

    // UPDATE atômico: debita apenas se quantity >= solicitado.
    // Retorna o número de linhas afetadas (1 = sucesso, 0 = estoque insuficiente).
    // @Modifying indica que a query altera dados (não é SELECT).
    @Modifying
    @Query("""
            UPDATE InventoryEntity i
               SET i.quantity = i.quantity - :quantity
             WHERE i.id.productId = :productId
               AND i.id.storeId   = :storeId
               AND i.quantity     >= :quantity
            """)
    int deductIfSufficient(@Param("productId") UUID productId,
                           @Param("storeId") UUID storeId,
                           @Param("quantity") int quantity);
}
