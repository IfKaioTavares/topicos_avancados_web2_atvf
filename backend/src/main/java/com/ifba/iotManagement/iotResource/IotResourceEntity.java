package com.ifba.iotManagement.iotResource;

import com.ifba.iotManagement.shared.AbstractEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Entity
@Table(name = "iot_resources")
@Getter
@AllArgsConstructor
public class IotResourceEntity extends AbstractEntity {
    private String resourceId;
    private String name;
    private String type;
    
    @Enumerated(EnumType.STRING)
    private IotResourceStatus status;
    
    private Long timeoutUsageInMinutes;
    private Boolean lockedForAdmin;

    public IotResourceEntity() {
        super();
    }
    
    public void updateStatus(IotResourceStatus newStatus) {
        this.status = newStatus;
        this.touch();
    }

    @Override
    protected void validate() {
        if(resourceId == null || resourceId.isBlank()){
            throw new IllegalStateException("Resource ID cannot be null or blank");
        }
        if(name == null || name.isBlank()){
            throw new IllegalStateException("Name cannot be null or blank");
        }
        if(type == null || type.isBlank()){
            throw new IllegalStateException("Type cannot be null or blank");
        }
        if(status == null){
            throw new IllegalStateException("Role cannot be null");
        }
    }
}
