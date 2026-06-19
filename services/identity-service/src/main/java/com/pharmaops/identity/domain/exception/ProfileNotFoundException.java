package com.pharmaops.identity.domain.exception;

public class ProfileNotFoundException extends RuntimeException {
    public ProfileNotFoundException(String identifier) {
        super("Profile not found: " + identifier);
    }
}
