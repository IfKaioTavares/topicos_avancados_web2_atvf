package com.ifba.iotManagement.user;

import com.ifba.iotManagement.shared.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends BaseRepository<UserEntity> {
    Optional<UserEntity> findByUsername(String username);
}
