package com.ifba.iotManagement.reports.dto;

import java.time.Instant;

public record ResourceUsageStatsDto(
        String resourceId,
        String resourceName,
        Long totalReservations,
        Long averageUsageMinutes,
        Instant lastUsed,
        Double utilizationRate
) {
}