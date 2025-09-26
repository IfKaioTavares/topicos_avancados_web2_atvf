package com.ifba.iotManagement.reports.dto;

import java.time.Instant;

public record SystemStatsDto(
        Long totalResources,
        Long activeResources,
        Long occupiedResources,
        Long activeReserves,
        Long totalUsers,
        Long todayReserves,
        String averageUsageTime,
        Long totalReserves,
        Double utilizationRate,
        SystemHealthDto systemHealth,
        String onlineUsers,
        Instant reportGeneratedAt
) {
    public record SystemHealthDto(
            Boolean database,
            Boolean apiServices
    ) {}
}