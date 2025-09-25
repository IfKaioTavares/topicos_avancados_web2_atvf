package com.ifba.iotManagement.iotResource.reserve.dto;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

public record CreateReserveRequestDto(
        @NotNull(message = "Resource ID cannot be null")
        UUID resourceId,
        
        @NotNull(message = "Start time cannot be null")
        Instant startTime,
        
        @NotNull(message = "Predicted end time cannot be null")
        Instant predictedEndTime
) {
}