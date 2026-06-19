package com.pharmaops.inventory.infrastructure.persistence.adapter;

import com.pharmaops.inventory.application.port.out.ProductRepository;
import com.pharmaops.inventory.domain.model.Product;
import com.pharmaops.inventory.infrastructure.persistence.entity.ProductEntity;
import com.pharmaops.inventory.infrastructure.persistence.repository.ProductJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProductRepositoryAdapter implements ProductRepository {

    private final ProductJpaRepository productJpaRepository;

    @Override
    public Product save(Product product) {
        return toDomain(productJpaRepository.save(toEntity(product)));
    }

    @Override
    public Optional<Product> findById(UUID id) {
        return productJpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Product> findByBarcode(String barcode) {
        return productJpaRepository.findByBarcode(barcode).map(this::toDomain);
    }

    @Override
    public boolean existsByBarcode(String barcode) {
        return productJpaRepository.existsByBarcode(barcode);
    }

    @Override
    public List<Product> findAll() {
        return productJpaRepository.findAll().stream().map(this::toDomain).toList();
    }

    private Product toDomain(ProductEntity e) {
        return Product.builder()
                .id(e.getId())
                .name(e.getName())
                .barcode(e.getBarcode())
                .controlled(e.isControlled())
                .salePrice(e.getSalePrice())
                .active(e.isActive())
                .build();
    }

    private ProductEntity toEntity(Product p) {
        return ProductEntity.builder()
                .id(p.getId())
                .name(p.getName())
                .barcode(p.getBarcode())
                .controlled(p.isControlled())
                .salePrice(p.getSalePrice())
                .active(p.isActive())
                .build();
    }
}
