package com.ifba.iotManagement.security.dtos;

import com.ifba.iotManagement.user.UserDto;

public record LoginResponseDto(
        UserDto user,
        String token
) {
}
