package com.ifba.iotManagement.iotResource;

import com.ifba.iotManagement.shared.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IotResourceRepository extends BaseRepository<IotResourceEntity> {
    Optional<IotResourceEntity> findByResourceIdAndDeletedIsFalse (String resourceId);
    List<IotResourceEntity> findAllByDeletedIsFalseAndLockedForAdminIsFalse();
    Optional<IotResourceEntity> findByPublicIdAndDeletedIsFalse(UUID publicId);
    List<IotResourceEntity> findAllByDeletedIsFalse();
    long countByDeletedIsFalse();
}