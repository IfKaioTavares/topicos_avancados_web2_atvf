package com.ifba.iotManagement.device;

import com.ifba.iotManagement.audit.AuditAction;
import com.ifba.iotManagement.audit.AuditResult;
import com.ifba.iotManagement.audit.AuditService;
import com.ifba.iotManagement.device.dto.DeviceAutoReleaseDto;
import com.ifba.iotManagement.device.dto.DeviceCommandDto;
import com.ifba.iotManagement.device.dto.DeviceResourceStatusDto;
import com.ifba.iotManagement.device.dto.DeviceStatusUpdateDto;
import com.ifba.iotManagement.iotResource.IotResourceEntity;
import com.ifba.iotManagement.iotResource.IotResourceRepository;
import com.ifba.iotManagement.iotResource.IotResourceStatus;
import com.ifba.iotManagement.iotResource.reserve.IotResourceReserveEntity;
import com.ifba.iotManagement.iotResource.reserve.IotResourceReserveRepository;
import com.ifba.iotManagement.shared.exceptions.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeviceService {
    
    private static final Logger logger = LoggerFactory.getLogger(DeviceService.class);
    
    private final IotResourceRepository resourceRepository;
    private final IotResourceReserveRepository reserveRepository;
    private final AuditService auditService;
    
    public DeviceService(IotResourceRepository resourceRepository, 
                        IotResourceReserveRepository reserveRepository,
                        AuditService auditService) {
        this.resourceRepository = resourceRepository;
        this.reserveRepository = reserveRepository;
        this.auditService = auditService;
    }
    
    @Transactional
    public void updateResourceStatus(DeviceStatusUpdateDto statusUpdate) {
        try {
            // Buscar recurso pelo resourceId
            IotResourceEntity resource = resourceRepository
                    .findByResourceIdAndDeletedIsFalse(statusUpdate.resourceId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Resource not found with resourceId: " + statusUpdate.resourceId()));
            
            // Verificar se é primeira conexão e dispositivo precisa ser ativado
            boolean wasActivated = handleFirstConnection(resource, statusUpdate);
            
            // Mapear status do dispositivo para enum
            IotResourceStatus newStatus;
            try {
                newStatus = mapDeviceStatusToEnum(statusUpdate.status());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid status from device: " + statusUpdate.status());
            }
            
            IotResourceStatus oldStatus = resource.getStatus();
            
            // Atualizar status apenas se mudou
            if (oldStatus != newStatus) {
                resource.updateStatus(newStatus);
                resourceRepository.save(resource);
                
                // Log de auditoria
                auditService.logSystemAction(
                        AuditAction.DEVICE_STATUS_UPDATE,
                        resource.getPublicId(),
                        resource.getName(),
                        AuditResult.SUCCESS,
                        String.format("Device updated status from %s to %s at %s%s", 
                                oldStatus, newStatus, statusUpdate.timestamp(),
                                wasActivated ? " (Device auto-activated)" : "")
                );
                
                logger.info("Device status updated for resource {} from {} to {}{}",
                        statusUpdate.resourceId(), oldStatus, newStatus,
                        wasActivated ? " (auto-activated)" : "");
            } else if (wasActivated) {
                // Apenas ativação sem mudança de status
                resourceRepository.save(resource);
                
                logger.info("Device auto-activated for resource {} without status change", 
                        statusUpdate.resourceId());
            }
            
        } catch (Exception e) {
            // Log erro de auditoria
            auditService.logSystemAction(
                    AuditAction.DEVICE_STATUS_UPDATE,
                    null,
                    statusUpdate.resourceId(),
                    AuditResult.FAILURE,
                    "Failed to update device status: " + e.getMessage()
            );
            
            logger.error("Error updating device status for resource {}", 
                    statusUpdate.resourceId(), e);
            throw e;
        }
    }
    
    public DeviceCommandDto generateReserveCommand(String resourceId) {
        try {
            IotResourceEntity resource = resourceRepository
                    .findByResourceIdAndDeletedIsFalse(resourceId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Resource not found with resourceId: " + resourceId));
            
            // Log comando enviado
            auditService.logSystemAction(
                    AuditAction.DEVICE_COMMAND_SENT,
                    resource.getPublicId(),
                    resource.getName(),
                    AuditResult.SUCCESS,
                    "RESERVE command sent to device"
            );
            
            return new DeviceCommandDto(resourceId, "RESERVE", "Reserve this resource");
            
        } catch (Exception e) {
            auditService.logSystemAction(
                    AuditAction.DEVICE_COMMAND_SENT,
                    null,
                    resourceId,
                    AuditResult.FAILURE,
                    "Failed to send RESERVE command: " + e.getMessage()
            );
            throw e;
        }
    }
    
    public DeviceCommandDto generateReleaseCommand(String resourceId) {
        try {
            IotResourceEntity resource = resourceRepository
                    .findByResourceIdAndDeletedIsFalse(resourceId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Resource not found with resourceId: " + resourceId));
            
            // Log comando enviado
            auditService.logSystemAction(
                    AuditAction.DEVICE_COMMAND_SENT,
                    resource.getPublicId(),
                    resource.getName(),
                    AuditResult.SUCCESS,
                    "RELEASE command sent to device"
            );
            
            return new DeviceCommandDto(resourceId, "RELEASE", "Release this resource");
            
        } catch (Exception e) {
            auditService.logSystemAction(
                    AuditAction.DEVICE_COMMAND_SENT,
                    null,
                    resourceId,
                    AuditResult.FAILURE,
                    "Failed to send RELEASE command: " + e.getMessage()
            );
            throw e;
        }
    }
    
    /**
     * Trata primeira conexão do dispositivo, ativando-o automaticamente se necessário
     * @param resource Recurso IoT
     * @param statusUpdate Dados de status do dispositivo
     * @return true se o dispositivo foi ativado, false caso contrário
     */
    private boolean handleFirstConnection(IotResourceEntity resource, DeviceStatusUpdateDto statusUpdate) {
        // Verificar se é primeira conexão
        Boolean isFirstConnection = statusUpdate.firstConnection();
        if (isFirstConnection == null || !isFirstConnection) {
            return false; // Não é primeira conexão
        }
        
        // Verificar se dispositivo está inativo (precisa ser ativado)
        if (resource.getStatus() == IotResourceStatus.INACTIVE) {
            logger.info("First connection detected for resource {} - Auto-activating device", 
                    statusUpdate.resourceId());
            
            // Ativar dispositivo mudando status para FREE
            resource.updateStatus(IotResourceStatus.FREE);
            
            // Log de auditoria específico para ativação
            auditService.logSystemAction(
                    AuditAction.DEVICE_STATUS_UPDATE,
                    resource.getPublicId(),
                    resource.getName(),
                    AuditResult.SUCCESS,
                    String.format("Device auto-activated on first connection at %s", 
                            statusUpdate.timestamp())
            );
            
            return true;
        }
        
        // Dispositivo já estava ativo
        logger.debug("First connection detected for resource {} but device is already active (status: {})", 
                statusUpdate.resourceId(), resource.getStatus());
        return false;
    }
    
    private IotResourceStatus mapDeviceStatusToEnum(String deviceStatus) {
        return switch (deviceStatus.toUpperCase()) {
            case "LIVRE", "FREE", "AVAILABLE" -> IotResourceStatus.FREE;
            case "OCUPADO", "OCCUPIED", "RESERVED", "BUSY" -> IotResourceStatus.RESERVED;
            case "INDISPONIVEL", "UNAVAILABLE", "INACTIVE", "OFFLINE" -> IotResourceStatus.INACTIVE;
            default -> throw new IllegalArgumentException("Unknown device status: " + deviceStatus);
        };
    }
    
    public DeviceResourceStatusDto getResourceStatus(String resourceId) {
        try {
            IotResourceEntity resource = resourceRepository
                    .findByResourceIdAndDeletedIsFalse(resourceId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Resource not found with resourceId: " + resourceId));
            
            // Buscar detalhes da reserva se o recurso estiver reservado
            DeviceResourceStatusDto.ReserveDetailsDto reserveDetails = null;
            if (resource.getStatus() == IotResourceStatus.RESERVED) {
                var activeReserves = reserveRepository.findByIotResourceIdAndActiveIsTrueAndDeletedIsFalse(resource.getId());
                if (!activeReserves.isEmpty()) {
                    var currentReserve = activeReserves.get(0); // Pegar a primeira reserva ativa
                    reserveDetails = new DeviceResourceStatusDto.ReserveDetailsDto(
                            currentReserve.getUser().getUsername(),
                            currentReserve.getStartTime(),
                            currentReserve.getPredictedEndTime()
                    );
                }
            }
            
            return new DeviceResourceStatusDto(
                    resource.getResourceId(),
                    resource.getStatus().name(),
                    reserveDetails
            );
            
        } catch (Exception e) {
            logger.error("Error getting resource status for device {}", resourceId, e);
            throw e;
        }
    }
    
    @Transactional
    public void processAutoRelease(String resourceId, DeviceAutoReleaseDto autoReleaseData) {
        try {
            IotResourceEntity resource = resourceRepository
                    .findByResourceIdAndDeletedIsFalse(resourceId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Resource not found with resourceId: " + resourceId));
            
            // Verificar se recurso está realmente reservado
            if (resource.getStatus() != IotResourceStatus.RESERVED) {
                logger.warn("Auto-release ignored for resource {} - current status: {}", 
                        resourceId, resource.getStatus());
                return;
            }
            
            // Liberar o recurso
            resource.updateStatus(IotResourceStatus.FREE);
            
            // Se há reserva ativa, finalizá-la
            var activeReserves = reserveRepository.findByIotResourceIdAndActiveIsTrueAndDeletedIsFalse(resource.getId());
            for (IotResourceReserveEntity reserve : activeReserves) {
                reserve.finishReserve();
                reserveRepository.save(reserve);
            }
            
            resourceRepository.save(resource);
            
            // Log de auditoria
            auditService.logSystemAction(
                    AuditAction.DEVICE_AUTO_RELEASE,
                    resource.getPublicId(),
                    resource.getName(),
                    AuditResult.SUCCESS,
                    String.format("Device auto-released resource at %s (reason: %s)", 
                            autoReleaseData.timestamp(), autoReleaseData.reason())
            );
            
            logger.info("Device auto-released resource {} (reason: {})", 
                    resourceId, autoReleaseData.reason());
            
        } catch (Exception e) {
            // Log erro de auditoria
            auditService.logSystemAction(
                    AuditAction.DEVICE_AUTO_RELEASE,
                    null,
                    resourceId,
                    AuditResult.FAILURE,
                    "Failed to process auto-release: " + e.getMessage()
            );
            
            logger.error("Error processing auto-release for resource {}", resourceId, e);
            throw e;
        }
    }
}