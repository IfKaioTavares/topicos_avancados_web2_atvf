package com.ifba.iotManagement.audit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuditService {
    
    private static final Logger logger = LoggerFactory.getLogger(AuditService.class);
    
    private final AuditLogRepository auditLogRepository;
    
    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }
    
    public void logAction(
            UUID userId,
            String username, 
            AuditAction action,
            UUID resourceId,
            String resourceName,
            AuditResult result,
            String details
    ) {
        logAction(userId, username, action, resourceId, resourceName, result, details, null);
    }
    
    public void logAction(
            UUID userId,
            String username,
            AuditAction action,
            UUID resourceId,
            String resourceName,
            AuditResult result,
            String details,
            String errorMessage
    ) {
        try {
            AuditLogEntity auditLog = new AuditLogEntity(
                    userId,
                    username,
                    action,
                    resourceId,
                    resourceName,
                    result,
                    details,
                    errorMessage
            );
            
            auditLogRepository.save(auditLog);
            
            // Log também no sistema de logs do Spring Boot para facilitar debugging
            if (result == AuditResult.SUCCESS) {
                logger.info("AUDIT: {} - User: {} ({}) - Resource: {} ({}) - Details: {}", 
                    action, username, userId, resourceName, resourceId, details);
            } else {
                logger.warn("AUDIT: {} - User: {} ({}) - Resource: {} ({}) - Details: {} - Error: {}", 
                    action, username, userId, resourceName, resourceId, details, errorMessage);
            }
            
        } catch (Exception e) {
            logger.error("Erro ao salvar log de auditoria", e);
        }
    }
    
    public void logSystemAction(AuditAction action, UUID resourceId, String resourceName, AuditResult result, String details) {
        logAction(null, "SYSTEM", action, resourceId, resourceName, result, details);
    }
}