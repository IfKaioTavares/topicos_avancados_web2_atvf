package com.ifba.iotManagement.web;

import com.ifba.iotManagement.shared.exceptions.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public final class RestExceptionHandler {

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    private ResponseEntity<RestErrorMessage> handleResourceAlreadyExistsException(ResourceAlreadyExistsException e) {
        var response = new RestErrorMessage(HttpStatus.CONFLICT, e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    private ResponseEntity<RestErrorMessage> handleResourceNotFoundException(ResourceNotFoundException e) {
        var response = new RestErrorMessage(HttpStatus.NOT_FOUND, e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    private ResponseEntity<RestErrorMessage> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }

        var response = new RestErrorMessage(
                HttpStatus.BAD_REQUEST,
                "Campos inválidos. Corrija os erros e tente novamente.",
                fieldErrors
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(UnauthorizedException.class)
    private ResponseEntity<RestErrorMessage> handleUnauthorizedException(UnauthorizedException e) {
        var response = new RestErrorMessage(HttpStatus.UNAUTHORIZED, e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    private ResponseEntity<RestErrorMessage> handleInvalidCredentialsException(BusinessException e) {
        var response = new RestErrorMessage(HttpStatus.BAD_REQUEST, e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    private ResponseEntity<RestErrorMessage> handleGenericException(Exception e) {
        var response = new RestErrorMessage(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Alguns problemas estão ocorrendo, nossa equipe já está resolvendo, tente novamente mais tarde"
        );
        log.error("Erro inesperado: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
