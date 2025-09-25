package com.ifba.iotManagement.device.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record DeviceStatusUpdateDto(
        @NotBlank(message = "Resource ID cannot be blank")
        String resourceId,
        
        @NotBlank(message = "Status cannot be blank")
        String status,
        
        @NotNull(message = "Timestamp cannot be null")
        Instant timestamp
) {
}