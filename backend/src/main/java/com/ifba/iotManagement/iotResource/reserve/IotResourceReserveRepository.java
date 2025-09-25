package com.ifba.iotManagement.iotResource.reserve;

import com.ifba.iotManagement.shared.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;

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
}
