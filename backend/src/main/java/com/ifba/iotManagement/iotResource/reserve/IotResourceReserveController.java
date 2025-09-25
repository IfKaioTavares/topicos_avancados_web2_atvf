package com.ifba.iotManagement.iotResource.reserve;

import com.ifba.iotManagement.iotResource.reserve.dto.CreateReserveRequestDto;
import com.ifba.iotManagement.iotResource.reserve.dto.ReserveDto;
import com.ifba.iotManagement.shared.BaseController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reserves")
@Tag(name = "Reserves", description = "Operações relacionadas a reservas de recursos IoT")
public class IotResourceReserveController extends BaseController {
    
    private final IotResourceReserveService reserveService;
    
    public IotResourceReserveController(IotResourceReserveService reserveService) {
        this.reserveService = reserveService;
    }
    
    @PostMapping
    @Operation(summary = "Criar reserva", description = "Cria uma nova reserva para um recurso IoT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Reserva criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou conflito de horário"),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado")
    })
    public ResponseEntity<ReserveDto> createReserve(
            @Valid @RequestBody CreateReserveRequestDto request,
            Authentication authentication) {
        
        UUID userId = UUID.fromString(authentication.getName());
        ReserveDto response = reserveService.createReserve(request, userId);
        
        return ResponseEntity.created(
                URI.create("/api/v1/reserves/%s".formatted(response.id()))
        ).body(response);
    }
    
    @PutMapping("/{reserveId}/release")
    @Operation(summary = "Liberar reserva", description = "Finaliza uma reserva ativa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reserva liberada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Reserva já está inativa"),
            @ApiResponse(responseCode = "403", description = "Usuário não pode liberar esta reserva"),
            @ApiResponse(responseCode = "404", description = "Reserva não encontrada")
    })
    public ResponseEntity<Void> releaseReserve(
            @PathVariable UUID reserveId,
            Authentication authentication) {
        
        UUID userId = UUID.fromString(authentication.getName());
        reserveService.releaseReserve(reserveId, userId);
        
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/my-active")
    @Operation(summary = "Listar reservas ativas do usuário", description = "Retorna todas as reservas ativas do usuário autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de reservas ativas retornada com sucesso")
    })
    public ResponseEntity<List<ReserveDto>> getMyActiveReserves(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        List<ReserveDto> reserves = reserveService.getActiveReservesByUser(userId);
        
        return ResponseEntity.ok(reserves);
    }
    
    @GetMapping("/my-history")
    @Operation(summary = "Histórico de reservas do usuário", description = "Retorna o histórico paginado de reservas do usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Histórico de reservas retornado com sucesso")
    })
    public ResponseEntity<Page<ReserveDto>> getMyReserveHistory(
            Authentication authentication,
            Pageable pageable) {
        
        UUID userId = UUID.fromString(authentication.getName());
        Page<ReserveDto> reserves = reserveService.getReserveHistory(userId, pageable);
        
        return ResponseEntity.ok(reserves);
    }
    
    @GetMapping("/history")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Histórico geral de reservas", description = "Retorna o histórico paginado de todas as reservas (admin apenas)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Histórico geral retornado com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado - apenas administradores")
    })
    public ResponseEntity<Page<ReserveDto>> getAllReservesHistory(Pageable pageable) {
        Page<ReserveDto> reserves = reserveService.getAllReservesHistory(pageable);
        return ResponseEntity.ok(reserves);
    }
    
    @GetMapping("/resource/{resourceId}")
    @Operation(summary = "Reservas de um recurso", description = "Retorna todas as reservas ativas de um recurso específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservas do recurso retornadas com sucesso"),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado")
    })
    public ResponseEntity<List<ReserveDto>> getReservesByResource(@PathVariable UUID resourceId) {
        List<ReserveDto> reserves = reserveService.getReservesByResource(resourceId);
        return ResponseEntity.ok(reserves);
    }
}