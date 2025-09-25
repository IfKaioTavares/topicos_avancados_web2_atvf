package com.ifba.iotManagement.iotResource.reserve;

import com.ifba.iotManagement.shared.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IotResourceReserveRepository extends BaseRepository<IotResourceReserveEntity> {

    @Query("""
        SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END
        FROM IotResourceReserveEntity r
        WHERE r.iotResource.id = :resourceId
            AND r.active = true
            AND r.deleted = false
            AND :startTime       < COALESCE(r.endTime, r.predictedEndTime)
            AND :predictedEndTime > r.startTime
        """)
    boolean existsConflict(
            @Param("resourceId") Long resourceId,
            @Param("startTime") Instant startTime,
            @Param("predictedEndTime") Instant predictedEndTime
    );

    @Query("""
        SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END
        FROM IotResourceReserveEntity r
        WHERE r.iotResource.id = :resourceId
            AND r.active = true
            AND r.deleted = false
            AND :startTime < COALESCE(r.endTime, r.predictedEndTime)
            AND :predictedEndTime > r.startTime
        """)
    boolean hasActiveReserveInTimeRange(
            @Param("resourceId") Long resourceId,
            @Param("startTime") Instant startTime,
            @Param("predictedEndTime") Instant predictedEndTime
    );

    Optional<IotResourceReserveEntity> findByPublicIdAndDeletedIsFalse(UUID publicId);

    List<IotResourceReserveEntity> findByUserIdAndActiveIsTrueAndDeletedIsFalse(Long userId);

    Page<IotResourceReserveEntity> findByUserIdAndDeletedIsFalseOrderByCreatedAtDesc(Long userId, Pageable pageable);

    Page<IotResourceReserveEntity> findAllByDeletedIsFalseOrderByCreatedAtDesc(Pageable pageable);

    List<IotResourceReserveEntity> findByIotResourceIdAndActiveIsTrueAndDeletedIsFalse(Long resourceId);

    @Query("""
        SELECT r FROM IotResourceReserveEntity r
        WHERE r.active = true
            AND r.deleted = false
            AND r.predictedEndTime < :currentTime
            AND r.endTime IS NULL
        """)
    List<IotResourceReserveEntity> findExpiredActiveReserves(@Param("currentTime") Instant currentTime);

    long countByDeletedIsFalse();
    
    long countByActiveIsTrueAndDeletedIsFalse();
    
    long countByIotResourceIdAndDeletedIsFalse(Long resourceId);
    
    List<IotResourceReserveEntity> findByIotResourceIdAndEndTimeIsNotNullAndDeletedIsFalse(Long resourceId);
    
    Optional<IotResourceReserveEntity> findTopByIotResourceIdAndDeletedIsFalseOrderByCreatedAtDesc(Long resourceId);
    
    long countByIotResourceIdAndCreatedAtAfterAndDeletedIsFalse(Long resourceId, Instant createdAt);
}
