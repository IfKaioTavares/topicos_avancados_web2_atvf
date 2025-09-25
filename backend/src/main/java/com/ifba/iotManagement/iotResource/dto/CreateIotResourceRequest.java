package com.ifba.iotManagement.iotResource.dto;

import com.ifba.iotManagement.iotResource.IotResourceEntity;
import com.ifba.iotManagement.iotResource.IotResourceStatus;

public record CreateIotResourceRequest(
        String resourceId,
        String name,
        String type,
        String status,
        Long timeoutUsageInMinutes,
        Boolean lockedForAdmin
) {
    public static IotResourceEntity toEntity(CreateIotResourceRequest request) {
        return new IotResourceEntity(
                request.resourceId(),
                request.name(),
                request.type(),
                Enum.valueOf(IotResourceStatus.class, request.status()),
                request.timeoutUsageInMinutes(),
                request.lockedForAdmin()
        );
    }
}
