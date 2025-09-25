package com.ifba.iotManagement.reports;

import com.ifba.iotManagement.reports.dto.ResourceUsageStatsDto;
import com.ifba.iotManagement.reports.dto.SystemStatsDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
@Tag(name = "Reports", description = "Relatórios e estatísticas do sistema")
public class ReportsController {
    
    private final ReportsService reportsService;
    
    public ReportsController(ReportsService reportsService) {
        this.reportsService = reportsService;
    }
    
    @GetMapping("/system-stats")
    @Operation(
            summary = "Estatísticas do sistema",
            description = "Retorna estatísticas gerais do sistema (recursos, reservas, usuários, utilização)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estatísticas retornadas com sucesso")
    })
    public ResponseEntity<SystemStatsDto> getSystemStats() {
        SystemStatsDto stats = reportsService.getSystemStats();
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/resource-usage")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Estatísticas de uso dos recursos",
            description = "Retorna estatísticas detalhadas de uso de cada recurso (admin apenas)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estatísticas de recursos retornadas com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado - apenas administradores")
    })
    public ResponseEntity<List<ResourceUsageStatsDto>> getResourceUsageStats() {
        List<ResourceUsageStatsDto> stats = reportsService.getResourceUsageStats();
        return ResponseEntity.ok(stats);
    }
}