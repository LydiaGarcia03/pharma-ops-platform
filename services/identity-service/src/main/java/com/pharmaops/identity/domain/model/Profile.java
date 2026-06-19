package com.pharmaops.identity.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.util.Set;
import java.util.UUID;

@Getter
@Builder
public class Profile {
    private final UUID id;
    private final String name;
    private final String description;
    @Builder.Default
    private final Set<Permission> permissions = Set.of();
}
