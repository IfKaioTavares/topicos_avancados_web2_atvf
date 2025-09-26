package com.ifba.iotManagement.iotResource;

import com.ifba.iotManagement.audit.AuditAction;
import com.ifba.iotManagement.audit.AuditResult;
import com.ifba.iotManagement.audit.AuditService;
import com.ifba.iotManagement.iotResource.dto.CreateIotResourceRequest;
import com.ifba.iotManagement.iotResource.dto.IotResourceDto;
import com.ifba.iotManagement.iotResource.dto.UpdateResourceStatusRequest;
import com.ifba.iotManagement.shared.exceptions.ResourceAlreadyExistsException;
import com.ifba.iotManagement.shared.exceptions.ResourceNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class IotResourceService {
    private final IotResourceRepository iotResourceRepository;
    private final AuditService auditService;
    
    public IotResourceService(IotResourceRepository iotResourceRepository, AuditService auditService) {
        this.iotResourceRepository = iotResourceRepository;
        this.auditService = auditService;
    }

    public void save(CreateIotResourceRequest request) {
        try {
            iotResourceRepository.findByResourceIdAndDeletedIsFalse(request.resourceId()).ifPresent(iotResource -> {
                throw new ResourceAlreadyExistsException("Iot com resourceId " + request.resourceId() + " já existe");
            });
            
            IotResourceEntity saved = iotResourceRepository.save(CreateIotResourceRequest.toEntity(request));
            
            auditService.logSystemAction(
                    AuditAction.RESOURCE_CREATED,
                    saved.getPublicId(),
                    saved.getName(),
                    AuditResult.SUCCESS,
                    "Resource created: " + saved.getResourceId()
            );
        } catch (Exception e) {
            auditService.logSystemAction(
                    AuditAction.RESOURCE_CREATED,
                    null,
                    request.name(),
                    AuditResult.FAILURE,
                    "Failed to create resource: " + e.getMessage()
            );
            throw e;
        }
    }

    public List<IotResourceDto> findAll() {
        List<IotResourceEntity> resources;
        
        // Admin pode ver todos os recursos, usuário regular apenas os não-admin
        if (isCurrentUserAdmin()) {
            resources = iotResourceRepository.findAllByDeletedIsFalse();
        } else {
            resources = iotResourceRepository.findAllByDeletedIsFalseAndLockedForAdminIsFalse();
        }
        
        return resources.stream()
                .map(IotResourceDto::fromEntity)
                .toList();
    }
    
    private boolean isCurrentUserAdmin() {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));
    }

    public IotResourceDto findById(UUID id) {
        IotResourceEntity resource = iotResourceRepository.findByPublicIdAndDeletedIsFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found with id: " + id));
        return IotResourceDto.fromEntity(resource);
    }

    @Transactional
    public void updateStatus(UUID id, UpdateResourceStatusRequest request) {
        try {
            IotResourceEntity resource = iotResourceRepository.findByPublicIdAndDeletedIsFalse(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Resource not found with id: " + id));
            
            IotResourceStatus oldStatus = resource.getStatus();
            IotResourceStatus newStatus;
            try {
                newStatus = IotResourceStatus.valueOf(request.status());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid status: " + request.status());
            }
            
            resource.updateStatus(newStatus);
            iotResourceRepository.save(resource);
            
            auditService.logSystemAction(
                    AuditAction.RESOURCE_STATUS_UPDATED,
                    resource.getPublicId(),
                    resource.getName(),
                    AuditResult.SUCCESS,
                    String.format("Status changed from %s to %s", oldStatus, newStatus)
            );
        } catch (Exception e) {
            auditService.logSystemAction(
                    AuditAction.RESOURCE_STATUS_UPDATED,
                    id,
                    "Unknown",
                    AuditResult.FAILURE,
                    "Failed to update resource status: " + e.getMessage()
            );
            throw e;
        }
    }

    @Transactional
    public void deleteResource(UUID id) {
        try {
            IotResourceEntity resource = iotResourceRepository.findByPublicIdAndDeletedIsFalse(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Resource not found with id: " + id));
            
            resource.markAsDeleted();
            iotResourceRepository.save(resource);
            
            auditService.logSystemAction(
                    AuditAction.RESOURCE_DELETED,
                    resource.getPublicId(),
                    resource.getName(),
                    AuditResult.SUCCESS,
                    "Resource deleted: " + resource.getResourceId()
            );
        } catch (Exception e) {
            auditService.logSystemAction(
                    AuditAction.RESOURCE_DELETED,
                    id,
                    "Unknown",
                    AuditResult.FAILURE,
                    "Failed to delete resource: " + e.getMessage()
            );
            throw e;
        }
    }
}
