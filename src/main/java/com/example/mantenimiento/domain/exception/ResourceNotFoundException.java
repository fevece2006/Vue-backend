package com.example.mantenimiento.domain.exception;

public class ResourceNotFoundException extends DomainException {
    public ResourceNotFoundException(String resourceType, String identifier) {
        super(String.format("%s no encontrado: %s", resourceType, identifier));
    }
}
