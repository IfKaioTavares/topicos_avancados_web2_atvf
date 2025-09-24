package com.ifba.iotManagement.shared;

import com.ifba.iotManagement.shared.exceptions.UnauthorizedException;
import org.springframework.security.core.Authentication;

import java.util.UUID;

public abstract class BaseController {
    protected void validateUser(Authentication authentication, UUID userId) {
        if (authentication == null || authentication.getName() == null) {
            throw new UnauthorizedException("Acesso negado: Usuário não autenticado.");
        }

        if (!authentication.getName().equals(userId.toString())) {
            throw new UnauthorizedException("Acesso negado: Usuário não autorizado.");
        }
    }
}
