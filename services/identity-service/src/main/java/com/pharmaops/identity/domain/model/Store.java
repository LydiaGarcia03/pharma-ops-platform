package com.pharmaops.identity.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class Store {
    private final UUID id;
    private final String name;
    private final String taxId;
    private final boolean active;
    private final LocalDateTime createdAt;
}
