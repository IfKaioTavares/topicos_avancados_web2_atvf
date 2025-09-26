package com.ifba.iotManagement.device;

import com.ifba.iotManagement.device.dto.DeviceAutoReleaseDto;
import com.ifba.iotManagement.device.dto.DeviceCommandDto;
import com.ifba.iotManagement.device.dto.DeviceResourceStatusDto;
import com.ifba.iotManagement.device.dto.DeviceStatusUpdateDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/devices")
@Tag(name = "Devices", description = "APIs para comunicação com dispositivos IoT")
public class DeviceController {
    
    private final DeviceService deviceService;
    
    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }
    
    @PostMapping("/status")
    @SecurityRequirements(value = {}) // Sem autenticação para dispositivos IoT
    @Operation(
            summary = "Atualizar status do dispositivo",
            description = "Recebe atualizações de status dos dispositivos IoT simuladores"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado")
    })
    public ResponseEntity<Void> updateStatus(@Valid @RequestBody DeviceStatusUpdateDto statusUpdate) {
        deviceService.updateResourceStatus(statusUpdate);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/{resourceId}/commands/reserve")
    @SecurityRequirements(value = {}) // Sem autenticação para dispositivos IoT
    @Operation(
            summary = "Obter comando de reserva",
            description = "Retorna comando para reservar um recurso específico"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comando gerado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado")
    })
    public ResponseEntity<DeviceCommandDto> getReserveCommand(@PathVariable String resourceId) {
        DeviceCommandDto command = deviceService.generateReserveCommand(resourceId);
        return ResponseEntity.ok(command);
    }
    
    @GetMapping("/{resourceId}/commands/release")
    @SecurityRequirements(value = {}) // Sem autenticação para dispositivos IoT
    @Operation(
            summary = "Obter comando de liberação",
            description = "Retorna comando para liberar um recurso específico"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comando gerado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado")
    })
    public ResponseEntity<DeviceCommandDto> getReleaseCommand(@PathVariable String resourceId) {
        DeviceCommandDto command = deviceService.generateReleaseCommand(resourceId);
        return ResponseEntity.ok(command);
    }
    
    @GetMapping("/{resourceId}/status")
    @SecurityRequirements(value = {}) // Sem autenticação para dispositivos IoT
    @Operation(
            summary = "Obter status atual do recurso",
            description = "Permite ao dispositivo consultar o status atual do recurso no backend"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status obtido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado")
    })
    public ResponseEntity<?> getResourceStatus(@PathVariable String resourceId) {
        return ResponseEntity.ok(deviceService.getResourceStatus(resourceId));
    }
    
    @PostMapping("/{resourceId}/auto-release")
    @SecurityRequirements(value = {}) // Sem autenticação para dispositivos IoT
    @Operation(
            summary = "Notificar auto-liberação",
            description = "Recebe notificação quando dispositivo executa auto-liberação por timeout"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Auto-liberação processada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado")
    })
    public ResponseEntity<Void> notifyAutoRelease(
            @PathVariable String resourceId,
            @Valid @RequestBody DeviceAutoReleaseDto autoReleaseData) {
        deviceService.processAutoRelease(resourceId, autoReleaseData);
        return ResponseEntity.ok().build();
    }
}