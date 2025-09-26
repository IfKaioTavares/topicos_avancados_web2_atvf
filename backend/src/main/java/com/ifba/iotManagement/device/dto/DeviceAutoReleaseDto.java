package com.ifba.iotManagement.device.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record DeviceAutoReleaseDto(
        @NotNull(message = "Timestamp cannot be null")
        Instant timestamp,
        
        @NotBlank(message = "Reason cannot be blank")
        String reason
) {
}