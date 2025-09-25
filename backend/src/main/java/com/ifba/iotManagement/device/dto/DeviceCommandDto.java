package com.ifba.iotManagement.device.dto;

public record DeviceCommandDto(
        String resourceId,
        String command,
        String details
) {
}