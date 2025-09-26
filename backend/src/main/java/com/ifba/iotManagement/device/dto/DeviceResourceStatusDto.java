package com.ifba.iotManagement.device.dto;

import java.time.Instant;

public record DeviceResourceStatusDto(
        String resourceId,
        String status,
        ReserveDetailsDto reserveDetails
) {
    public record ReserveDetailsDto(
            String userId,
            Instant startTime,
            Instant predictedEndTime
    ) {
    }
}