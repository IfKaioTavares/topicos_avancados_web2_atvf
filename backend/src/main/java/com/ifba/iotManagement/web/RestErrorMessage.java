package com.ifba.iotManagement.web;

import org.springframework.http.HttpStatus;
import java.time.LocalDateTime;
import java.util.Map;

public record RestErrorMessage(
        HttpStatus status,
        String message,
        LocalDateTime timestamp,
        Map<String, String> errors
) {
    public RestErrorMessage(HttpStatus status, String message, Map<String, String> errors) {
        this(status, message, LocalDateTime.now(), errors);
    }

    public RestErrorMessage(HttpStatus status, String message) {
        this(status, message, LocalDateTime.now(), null);
    }
}
