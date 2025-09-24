package com.ifba.iotManagement.shared.exceptions;

public final class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
