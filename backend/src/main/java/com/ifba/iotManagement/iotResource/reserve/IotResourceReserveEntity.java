package com.ifba.iotManagement.iotResource.reserve;

import com.ifba.iotManagement.iotResource.IotResourceEntity;
import com.ifba.iotManagement.shared.AbstractEntity;
import com.ifba.iotManagement.user.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Entity
@Table(name = "iot_resource_reserves")
@Getter
@AllArgsConstructor
public class IotResourceReserveEntity extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne(fetch =  FetchType.LAZY)
    @JoinColumn(name = "resource_id")
    private IotResourceEntity iotResource;
    private Instant startTime;
    private Instant predictedEndTime;
    private Instant endTime;
    private Boolean active;


    public IotResourceReserveEntity() {
        super();
    }

    @Override
    protected void validate() {
        if(user == null){
            throw new IllegalStateException("User cannot be null");
        }
        if(iotResource == null){
            throw new IllegalStateException("IoT Resource cannot be null");
        }
        if(startTime == null){
            throw new IllegalStateException("Start time cannot be null");
        }
        if(predictedEndTime == null){
            throw new IllegalStateException("Predicted end time cannot be null");
        }
        if(active == null){
            throw new IllegalStateException("Active cannot be null");
        }

    }
}
