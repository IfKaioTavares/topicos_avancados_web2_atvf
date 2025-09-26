package com.ifba.iotManagement.reports;

import com.ifba.iotManagement.iotResource.IotResourceRepository;
import com.ifba.iotManagement.iotResource.IotResourceStatus;
import com.ifba.iotManagement.iotResource.reserve.IotResourceReserveRepository;
import com.ifba.iotManagement.reports.dto.ResourceUsageStatsDto;
import com.ifba.iotManagement.reports.dto.SystemStatsDto;
import com.ifba.iotManagement.user.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
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
        // Contadores básicos - para usuários regulares, considera apenas recursos não-admin
        Long totalResources = getCurrentUserRole().equals("ADMIN") 
                ? resourceRepository.countByDeletedIsFalse()
                : resourceRepository.countByDeletedIsFalseAndLockedForAdminIsFalse();
        
        Long totalUsers = userRepository.countByDeletedIsFalse();
        Long totalReserves = reserveRepository.countByDeletedIsFalse();
        Long activeReserves = reserveRepository.countByActiveIsTrueAndDeletedIsFalse();
        
        // Recursos por status
        Long activeResources = resourceRepository.countByStatusAndDeletedIsFalse(IotResourceStatus.FREE);
        Long occupiedResources = resourceRepository.countByStatusAndDeletedIsFalse(IotResourceStatus.RESERVED);
        
        // Reservas de hoje
        Instant startOfDay = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant endOfDay = LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
        Long todayReserves = reserveRepository.countByCreatedAtBetweenAndDeletedIsFalse(startOfDay, endOfDay);
        
        // Tempo médio de uso das reservas finalizadas
        String averageUsageTime = calculateOverallAverageUsageTime();
        
        // Taxa de utilização (recursos ocupados / total de recursos)
        Double utilizationRate = totalResources > 0 
                ? (occupiedResources.doubleValue() / totalResources.doubleValue()) * 100
                : 0.0;
        
        // Status do sistema (simulado - em um ambiente real isso viria de health checks)
        SystemStatsDto.SystemHealthDto systemHealth = new SystemStatsDto.SystemHealthDto(
                true, // database - assumimos que está conectado se chegamos até aqui
                true  // apiServices - assumimos que estão operacionais
        );
        
        return new SystemStatsDto(
                totalResources,
                activeResources,
                occupiedResources,
                activeReserves,
                totalUsers,
                todayReserves,
                averageUsageTime,
                totalReserves,
                utilizationRate,
                systemHealth,
                "N/A", // onlineUsers - seria necessário implementar sessões para isso
                Instant.now()
        );
    }
    
    private String calculateOverallAverageUsageTime() {
        var finishedReserves = reserveRepository.findByEndTimeIsNotNullAndDeletedIsFalse();
        
        if (finishedReserves.isEmpty()) {
            return "0h";
        }
        
        long totalMinutes = finishedReserves.stream()
                .mapToLong(reserve -> ChronoUnit.MINUTES.between(reserve.getStartTime(), reserve.getEndTime()))
                .sum();
        
        long averageMinutes = totalMinutes / finishedReserves.size();
        
        if (averageMinutes < 60) {
            return averageMinutes + "m";
        } else {
            long hours = averageMinutes / 60;
            long remainingMinutes = averageMinutes % 60;
            return remainingMinutes > 0 ? hours + "h " + remainingMinutes + "m" : hours + "h";
        }
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
    
    private String getCurrentUserRole() {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .filter(role -> role.equals("ROLE_ADMIN") || role.equals("ROLE_USER"))
                .map(role -> role.replace("ROLE_", ""))
                .findFirst()
                .orElse("USER");
    }
}