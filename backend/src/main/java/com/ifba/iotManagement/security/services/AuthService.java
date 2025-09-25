package com.ifba.iotManagement.security.services;

import com.ifba.iotManagement.security.dtos.LoginRequestDto;
import com.ifba.iotManagement.security.dtos.LoginResponseDto;
import com.ifba.iotManagement.shared.exceptions.InvalidCredentialsException;
import com.ifba.iotManagement.user.UserDto;
import com.ifba.iotManagement.user.UserRepository;
import com.ifba.iotManagement.user.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class AuthService {
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(JwtService jwtService, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        var user = userRepository.findByUsername(loginRequestDto.username())
                .orElseThrow(() -> new InvalidCredentialsException("Username ou senha inválidos"));
        if (!passwordEncoder.matches(loginRequestDto.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Username ou senha inválidos");
        }
        String token = jwtService.generateToken(user.getPublicId(), createAuthorities(user.getRole().name()));
        return new LoginResponseDto(UserDto.fromEntity(user), token);
    }

    private List<GrantedAuthority> createAuthorities(String role) {
        final List<GrantedAuthority> authorities = new ArrayList<>();
        if(role != null && !role.trim().isEmpty()) {
            authorities.add(new SimpleGrantedAuthority(role.toUpperCase()));

            if(UserRole.ADMIN.name().equals(role)) {
                authorities.add(new SimpleGrantedAuthority(UserRole.USER.name()));
            }
        }
        return authorities;
    }
}
