package com.ifba.iotManagement.user;

import com.ifba.iotManagement.shared.AbstractEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Getter
@AllArgsConstructor
@Table(name = "users")
public class UserEntity extends AbstractEntity {
    private  String username;
    private String passwordHash;
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private UserRole role;

    public UserEntity() {
        super();
    }

    @Override
    protected void validate() {
        if(username == null || username.isBlank()){
            throw new IllegalStateException("Username cannot be null or blank");
        }
        if(passwordHash == null || passwordHash.isBlank()){
            throw new IllegalStateException("Password hash cannot be null or blank");
        }
        if(role == null){
            throw new IllegalStateException("Role cannot be null");
        }
    }
}
