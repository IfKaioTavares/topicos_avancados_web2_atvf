package com.ifba.iotManagement.shared.exceptions;

public final class ResourceAlreadyExistsException extends RuntimeException {
    public ResourceAlreadyExistsException(String message) {
        super(message);
    }
    public ResourceAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

}
