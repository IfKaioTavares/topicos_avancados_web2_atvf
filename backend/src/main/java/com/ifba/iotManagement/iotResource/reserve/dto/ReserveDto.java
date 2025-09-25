package com.ifba.iotManagement.iotResource.reserve.dto;

import com.ifba.iotManagement.iotResource.reserve.IotResourceReserveEntity;

import java.time.Instant;
import java.util.UUID;

public record ReserveDto(
        UUID id,
        UUID userId,
        String username,
        UUID resourceId,
        String resourceName,
        Instant startTime,
        Instant predictedEndTime,
        Instant endTime,
        Boolean active
) {
    public static ReserveDto fromEntity(IotResourceReserveEntity entity) {
        return new ReserveDto(
                entity.getPublicId(),
                entity.getUser().getPublicId(),
                entity.getUser().getUsername(),
                entity.getIotResource().getPublicId(),
                entity.getIotResource().getName(),
                entity.getStartTime(),
                entity.getPredictedEndTime(),
                entity.getEndTime(),
                entity.getActive()
        );
    }
}