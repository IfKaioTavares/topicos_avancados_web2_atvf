package com.ifba.iotManagement.user;

import com.ifba.iotManagement.shared.exceptions.ResourceAlreadyExistsException;
import com.ifba.iotManagement.shared.exceptions.ResourceNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public final class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDto createUser(CreateUserRequestDto dto) {
        userRepository.findByUsername(dto.username()).ifPresent(user -> {
            throw new ResourceAlreadyExistsException("Username já está em uso");
        });

        String encodedPassword = passwordEncoder.encode(dto.password());
        UserEntity user = new UserEntity(dto.username(), encodedPassword, UserRole.USER);
        userRepository.save(user);
        return UserDto.fromEntity(user);
    }

    public UserDto getUserByPublicId(UUID publicId){
        UserEntity user = userRepository.findByPublicIdAndDeletedFalse(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário com ID " + publicId + " não encontrado"));
        return UserDto.fromEntity(user);
    }
}
