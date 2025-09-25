package com.ifba.iotManagement.iotResource;

import com.ifba.iotManagement.shared.AbstractEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "iot_resources")
@Getter
@AllArgsConstructor
public class IotResourceEntity extends AbstractEntity {
    private String resourceId;
    private String name;
    private String type;
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private IotResourceStatus status;
    private Long timeoutUsageInMinutes;
    private Boolean lockedForAdmin;

    public IotResourceEntity() {
        super();
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
