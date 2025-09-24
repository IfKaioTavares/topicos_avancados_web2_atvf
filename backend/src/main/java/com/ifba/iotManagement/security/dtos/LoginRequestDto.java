package com.ifba.iotManagement.security.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequestDto (
        @NotBlank(message = "Username não pode ser vazio")
        @Size(min = 5, max = 50, message = "Username deve ter entre 5 e 50 caracteres")
        String username,
        @NotBlank(message = "Password não pode ser vazio")
        @Size(min = 8, max = 255, message = "Password deve ter entre 8 e 255 caracteres")
        String password
) {}
