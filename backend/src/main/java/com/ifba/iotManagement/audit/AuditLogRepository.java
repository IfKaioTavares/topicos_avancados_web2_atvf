package com.ifba.iotManagement.audit;

import com.ifba.iotManagement.shared.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface AuditLogRepository extends BaseRepository<AuditLogEntity> {
    
    Page<AuditLogEntity> findByDeletedIsFalseOrderByCreatedAtDesc(Pageable pageable);
    
    Page<AuditLogEntity> findByUserIdAndDeletedIsFalseOrderByCreatedAtDesc(UUID userId, Pageable pageable);
    
    Page<AuditLogEntity> findByResourceIdAndDeletedIsFalseOrderByCreatedAtDesc(UUID resourceId, Pageable pageable);
    
    List<AuditLogEntity> findByActionAndDeletedIsFalseOrderByCreatedAtDesc(AuditAction action);
    
    @Query("""
        SELECT a FROM AuditLogEntity a
        WHERE a.deleted = false
            AND a.createdAt >= :startDate
            AND a.createdAt <= :endDate
        ORDER BY a.createdAt DESC
        """)
    List<AuditLogEntity> findByCreatedAtBetween(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);
    
    long countByActionAndResultAndDeletedIsFalse(AuditAction action, AuditResult result);
}