package com.ifba.iotManagement.iotResource.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateResourceStatusRequest(
        @NotBlank(message = "Status cannot be blank")
        String status
) {
}