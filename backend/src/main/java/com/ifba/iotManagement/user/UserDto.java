package com.ifba.iotManagement.user;

import java.time.Instant;
import java.util.UUID;

public record UserDto(
        UUID id,
        String username,
        String role,
        Instant createdAt,
        Instant updatedAt
) {
    public static UserDto fromEntity(UserEntity user) {
        return new UserDto(
                user.getPublicId(),
                user.getUsername(),
                user.getRole().name(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
