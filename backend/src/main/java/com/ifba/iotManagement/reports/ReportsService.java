package com.ifba.iotManagement.reports;

import com.ifba.iotManagement.iotResource.IotResourceRepository;
import com.ifba.iotManagement.iotResource.reserve.IotResourceReserveRepository;
import com.ifba.iotManagement.reports.dto.ResourceUsageStatsDto;
import com.ifba.iotManagement.reports.dto.SystemStatsDto;
import com.ifba.iotManagement.user.UserRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportsService {
    
    private final IotResourceRepository resourceRepository;
    private final IotResourceReserveRepository reserveRepository;
    private final UserRepository userRepository;
    
    public ReportsService(
            IotResourceRepository resourceRepository,
            IotResourceReserveRepository reserveRepository,
            UserRepository userRepository
    ) {
        this.resourceRepository = resourceRepository;
        this.reserveRepository = reserveRepository;
        this.userRepository = userRepository;
    }
    
    public SystemStatsDto getSystemStats() {
        Long totalResources = resourceRepository.countByDeletedIsFalse();
        Long totalReservations = reserveRepository.countByDeletedIsFalse();
        Long activeReservations = reserveRepository.countByActiveIsTrueAndDeletedIsFalse();
        Long totalUsers = userRepository.countByDeletedIsFalse();
        
        // Calcular taxa de utilização (reservas ativas / total de recursos)
        Double systemUtilizationRate = totalResources > 0 
                ? (activeReservations.doubleValue() / totalResources.doubleValue()) * 100
                : 0.0;
        
        return new SystemStatsDto(
                totalResources,
                totalReservations,
                activeReservations,
                totalUsers,
                systemUtilizationRate,
                Instant.now()
        );
    }
    
    public List<ResourceUsageStatsDto> getResourceUsageStats() {
        return resourceRepository.findAllByDeletedIsFalse()
                .stream()
                .map(resource -> {
                    // Contar total de reservas para o recurso
                    Long totalReservations = reserveRepository.countByIotResourceIdAndDeletedIsFalse(resource.getId());
                    
                    // Calcular tempo médio de uso (baseado em reservas finalizadas)
                    Long averageUsageMinutes = calculateAverageUsageMinutes(resource.getId());
                    
                    // Encontrar última utilização
                    Instant lastUsed = findLastUsage(resource.getId());
                    
                    // Calcular taxa de utilização (reservas nos últimos 30 dias / dias disponíveis)
                    Double utilizationRate = calculateUtilizationRate(resource.getId());
                    
                    return new ResourceUsageStatsDto(
                            resource.getResourceId(),
                            resource.getName(),
                            totalReservations,
                            averageUsageMinutes,
                            lastUsed,
                            utilizationRate
                    );
                })
                .collect(Collectors.toList());
    }
    
    private Long calculateAverageUsageMinutes(Long resourceId) {
        // Implementação simplificada - poderia ser feita com uma query agregada mais eficiente
        var finishedReserves = reserveRepository.findByIotResourceIdAndEndTimeIsNotNullAndDeletedIsFalse(resourceId);
        
        if (finishedReserves.isEmpty()) {
            return 0L;
        }
        
        long totalMinutes = finishedReserves.stream()
                .mapToLong(reserve -> ChronoUnit.MINUTES.between(reserve.getStartTime(), reserve.getEndTime()))
                .sum();
        
        return totalMinutes / finishedReserves.size();
    }
    
    private Instant findLastUsage(Long resourceId) {
        return reserveRepository.findTopByIotResourceIdAndDeletedIsFalseOrderByCreatedAtDesc(resourceId)
                .map(reserve -> reserve.getCreatedAt())
                .orElse(null);
    }
    
    private Double calculateUtilizationRate(Long resourceId) {
        Instant thirtyDaysAgo = Instant.now().minus(30, ChronoUnit.DAYS);
        
        Long recentReservations = reserveRepository.countByIotResourceIdAndCreatedAtAfterAndDeletedIsFalse(
                resourceId, thirtyDaysAgo);
        
        // Taxa baseada em número de reservas nos últimos 30 dias
        // Máximo teórico seria 30 reservas (1 por dia)
        return Math.min((recentReservations.doubleValue() / 30.0) * 100, 100.0);
    }
}