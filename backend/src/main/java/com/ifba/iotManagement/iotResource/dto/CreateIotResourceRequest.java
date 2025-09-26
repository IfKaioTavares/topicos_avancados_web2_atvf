package com.ifba.iotManagement.iotResource.dto;

import com.ifba.iotManagement.iotResource.IotResourceEntity;
import com.ifba.iotManagement.iotResource.IotResourceStatus;

import java.util.Locale;
import java.util.Objects;

public record CreateIotResourceRequest(
        String resourceId,
        String name,
        String type,
        String status,
        Long timeoutUsageInMinutes,
        Boolean lockedForAdmin
) {
    public static IotResourceEntity toEntity(CreateIotResourceRequest request) {
        String resourceId = requireValue(request.resourceId(), "resourceId");
        String name = requireValue(request.name(), "name");
        String type = requireValue(request.type(), "type");

        IotResourceStatus status = resolveStatus(request.status());
        long timeout = resolveTimeout(request.timeoutUsageInMinutes());
        boolean lockedForAdmin = Boolean.TRUE.equals(request.lockedForAdmin());

        return new IotResourceEntity(
                resourceId,
                name,
                type,
                status,
                timeout,
                lockedForAdmin
        );
    }

    private static String requireValue(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Campo obrigatório não informado: " + fieldName);
        }
        return value.trim();
    }

    private static IotResourceStatus resolveStatus(String status) {
        if (status == null || status.isBlank()) {
            return IotResourceStatus.INACTIVE;
        }

        try {
            return IotResourceStatus.valueOf(status.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Status inválido: " + status);
        }
    }

    private static long resolveTimeout(Long timeoutUsageInMinutes) {
        if (Objects.isNull(timeoutUsageInMinutes) || timeoutUsageInMinutes <= 0) {
            return 60L;
        }
        return timeoutUsageInMinutes;
    }
}
