package com.ifba.iotManagement.iotResource;

import com.ifba.iotManagement.iotResource.dto.CreateIotResourceRequest;
import com.ifba.iotManagement.iotResource.dto.IotResourceDto;
import com.ifba.iotManagement.iotResource.dto.UpdateResourceStatusRequest;
import com.ifba.iotManagement.shared.BaseController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/iot-resources")
@Tag(name = "IoT Resources", description = "Operações relacionadas aos recursos IoT")
public class IotResourceController extends BaseController {
    private final IotResourceService iotResourceService;
    public IotResourceController(IotResourceService iotResourceService) {
        this.iotResourceService = iotResourceService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Criar recurso IoT", description = "Cria um novo recurso IoT no sistema (admin apenas)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recurso criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "403", description = "Acesso negado - apenas administradores")
    })
    public ResponseEntity<Object> createIotResource(@RequestBody CreateIotResourceRequest request) {
        iotResourceService.save(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    @Operation(summary = "Listar recursos IoT", description = "Retorna todos os recursos IoT disponíveis")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de recursos retornada com sucesso")
    })
    public ResponseEntity<List<IotResourceDto>> findAll() {
        return ResponseEntity.ok(iotResourceService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar recurso por ID", description = "Retorna um recurso IoT específico pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recurso encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado")
    })
    public ResponseEntity<IotResourceDto> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(iotResourceService.findById(id));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Atualizar status do recurso", description = "Atualiza o status de um recurso IoT (admin apenas)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Status inválido"),
            @ApiResponse(responseCode = "403", description = "Acesso negado - apenas administradores"),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado")
    })
    public ResponseEntity<Void> updateResourceStatus(@PathVariable UUID id, @RequestBody UpdateResourceStatusRequest request) {
        iotResourceService.updateStatus(id, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deletar recurso", description = "Remove um recurso IoT do sistema (admin apenas)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recurso removido com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado - apenas administradores"),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado")
    })
    public ResponseEntity<Void> deleteResource(@PathVariable UUID id) {
        iotResourceService.deleteResource(id);
        return ResponseEntity.ok().build();
    }
}
