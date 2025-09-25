package com.ifba.iotManagement.reports.dto;

import java.time.Instant;

public record SystemStatsDto(
        Long totalResources,
        Long totalReservations,
        Long activeReservations,
        Long totalUsers,
        Double systemUtilizationRate,
        Instant reportGeneratedAt
) {
}