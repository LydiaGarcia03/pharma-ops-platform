package com.pharmaops.identity.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class Permission {
    private final UUID id;
    private final String name;
    private final String description;
}
