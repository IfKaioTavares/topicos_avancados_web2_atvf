package com.ifba.iotManagement.shared.exceptions;

public final class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
