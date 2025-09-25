package com.ifba.iotManagement.device;

import com.ifba.iotManagement.audit.AuditAction;
import com.ifba.iotManagement.audit.AuditResult;
import com.ifba.iotManagement.audit.AuditService;
import com.ifba.iotManagement.device.dto.DeviceCommandDto;
import com.ifba.iotManagement.device.dto.DeviceStatusUpdateDto;
import com.ifba.iotManagement.iotResource.IotResourceEntity;
import com.ifba.iotManagement.iotResource.IotResourceRepository;
import com.ifba.iotManagement.iotResource.IotResourceStatus;
import com.ifba.iotManagement.shared.exceptions.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeviceService {
    
    private static final Logger logger = LoggerFactory.getLogger(DeviceService.class);
    
    private final IotResourceRepository resourceRepository;
    private final AuditService auditService;
    
    public DeviceService(IotResourceRepository resourceRepository, AuditService auditService) {
        this.resourceRepository = resourceRepository;
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
                        String.format("Device updated status from %s to %s at %s", 
                                oldStatus, newStatus, statusUpdate.timestamp())
                );
                
                logger.info("Device status updated for resource {} from {} to {}", 
                        statusUpdate.resourceId(), oldStatus, newStatus);
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
    
    private IotResourceStatus mapDeviceStatusToEnum(String deviceStatus) {
        return switch (deviceStatus.toUpperCase()) {
            case "LIVRE", "FREE", "AVAILABLE" -> IotResourceStatus.FREE;
            case "OCUPADO", "OCCUPIED", "RESERVED", "BUSY" -> IotResourceStatus.RESERVED;
            case "INDISPONIVEL", "UNAVAILABLE", "INACTIVE", "OFFLINE" -> IotResourceStatus.INACTIVE;
            default -> throw new IllegalArgumentException("Unknown device status: " + deviceStatus);
        };
    }
}