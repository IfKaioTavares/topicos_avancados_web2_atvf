package com.ifba.iotManagement.iotResource.dto;

import com.ifba.iotManagement.iotResource.IotResourceEntity;

import java.util.UUID;

public record IotResourceDto(
        UUID id,
        String resourceId,
        String name,
        String type,
        String role,
        Long timeoutUsageInMinutes
) {
    public static IotResourceDto fromEntity(IotResourceEntity iotResource) {
        return new IotResourceDto(
                iotResource.getPublicId(),
                iotResource.getResourceId(),
                iotResource.getName(),
                iotResource.getType(),
                iotResource.getStatus().name(),
                iotResource.getTimeoutUsageInMinutes()
        );
    }
}
