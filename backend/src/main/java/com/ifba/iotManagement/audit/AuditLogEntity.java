package com.ifba.iotManagement.audit;

import com.ifba.iotManagement.shared.AbstractEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;


import java.util.UUID;

@Entity
@Table(name = "audit_logs")
@Getter
@AllArgsConstructor
public class AuditLogEntity extends AbstractEntity {
    
    private UUID userId;
    private String username;
    
    @Enumerated(EnumType.STRING)
    private AuditAction action;
    
    private UUID resourceId;
    private String resourceName;
    
    @Enumerated(EnumType.STRING)
    private AuditResult result;
    
    private String details;
    private String errorMessage;
    
    public AuditLogEntity() {
        super();
    }

    @Override
    protected void validate() {
        if (action == null) {
            throw new IllegalStateException("Action cannot be null");
        }
        if (result == null) {
            throw new IllegalStateException("Result cannot be null");
        }
    }
}