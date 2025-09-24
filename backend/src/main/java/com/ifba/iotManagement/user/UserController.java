package com.ifba.iotManagement.user;

import com.ifba.iotManagement.shared.BaseController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;


@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User", description = "Operações relacionadas a usuários")
public final class UserController extends BaseController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @Operation(summary = "Criar usuário", description = "Cria um novo usuário no sistema.")
    @SecurityRequirements(value = {})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
    })
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody CreateUserRequestDto dto){
        var response = userService.createUser(dto);
        return ResponseEntity.created(
                URI.create("/api/v1/users/%s".formatted(response.id()))
        ).body(response);
    }


    @Operation(
            summary = "Buscar usuário por ID",
            description = "Retorna as informações de um usuário a partir do seu UUID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado com sucesso")
    })
    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserById(@PathVariable UUID userId, Authentication authentication){
        validateUser(authentication, userId);
        var response = userService.getUserByPublicId(userId);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/me")
    @Operation(
            summary = "Buscar usuário autenticado",
            description = "Retorna os dados do usuário autenticado."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário autenticado encontrado"),
    })
    public ResponseEntity<UserDto> getCurrentUser(Authentication authentication){
        final var userId = authentication.getName();
        if(userId == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        var response = userService.getUserByPublicId(UUID.fromString(userId));
        return ResponseEntity.ok(response);
    }
}
