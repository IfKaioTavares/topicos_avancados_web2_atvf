package com.ifba.iotManagement.iotResource.reserve;

import com.ifba.iotManagement.audit.AuditAction;
import com.ifba.iotManagement.audit.AuditResult;
import com.ifba.iotManagement.audit.AuditService;
import com.ifba.iotManagement.iotResource.IotResourceEntity;
import com.ifba.iotManagement.iotResource.IotResourceRepository;
import com.ifba.iotManagement.iotResource.IotResourceStatus;
import com.ifba.iotManagement.iotResource.reserve.dto.CreateReserveRequestDto;
import com.ifba.iotManagement.iotResource.reserve.dto.ReserveDto;
import com.ifba.iotManagement.shared.exceptions.ResourceNotFoundException;
import com.ifba.iotManagement.user.UserEntity;
import com.ifba.iotManagement.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class IotResourceReserveService {
    
    private final IotResourceReserveRepository reserveRepository;
    private final IotResourceRepository resourceRepository;
    private final UserRepository userRepository;
    private final AuditService auditService;
    
    public IotResourceReserveService(
            IotResourceReserveRepository reserveRepository,
            IotResourceRepository resourceRepository,
            UserRepository userRepository,
            AuditService auditService
    ) {
        this.reserveRepository = reserveRepository;
        this.resourceRepository = resourceRepository;
        this.userRepository = userRepository;
        this.auditService = auditService;
    }
    
    @Transactional
    public ReserveDto createReserve(CreateReserveRequestDto request, UUID userId) {
        try {
            // Buscar usuário
            UserEntity user = userRepository.findByPublicIdAndDeletedIsFalse(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            
            // Buscar recurso
            IotResourceEntity resource = resourceRepository.findByPublicIdAndDeletedIsFalse(request.resourceId())
                    .orElseThrow(() -> new ResourceNotFoundException("Resource not found"));
            
            // Validar se recurso está disponível
            if (resource.getStatus() != IotResourceStatus.FREE) {
                throw new IllegalStateException("Resource is not available for reservation");
            }
            
            // Validar se não há conflito de horário
            boolean hasConflict = reserveRepository.hasActiveReserveInTimeRange(
                    resource.getId(),
                    request.startTime(),
                    request.predictedEndTime()
            );
            
            if (hasConflict) {
                throw new IllegalStateException("There is already an active reservation for this resource in the requested time range");
            }
            
            // Criar reserva
            IotResourceReserveEntity reserve = new IotResourceReserveEntity(
                    user,
                    resource,
                    request.startTime(),
                    request.predictedEndTime(),
                    null,
                    true
            );
            
            // Salvar reserva
            IotResourceReserveEntity savedReserve = reserveRepository.save(reserve);
            
            // Atualizar status do recurso se a reserva começar agora
            if (request.startTime().isBefore(Instant.now()) || request.startTime().equals(Instant.now())) {
                resource.updateStatus(IotResourceStatus.RESERVED);
                resourceRepository.save(resource);
            }
            
            // Log de auditoria
            auditService.logAction(
                    user.getPublicId(),
                    user.getUsername(),
                    AuditAction.RESERVE_CREATED,
                    resource.getPublicId(),
                    resource.getName(),
                    AuditResult.SUCCESS,
                    String.format("Reserve created from %s to %s", request.startTime(), request.predictedEndTime())
            );
            
            return ReserveDto.fromEntity(savedReserve);
        } catch (Exception e) {
            // Log de erro de auditoria
            auditService.logAction(
                    userId,
                    "Unknown",
                    AuditAction.RESERVE_CREATED,
                    request.resourceId(),
                    "Unknown",
                    AuditResult.FAILURE,
                    "Failed to create reserve: " + e.getMessage()
            );
            throw e;
        }
    }
    
    @Transactional
    public void releaseReserve(UUID reserveId, UUID userId) {
        IotResourceReserveEntity reserve = reserveRepository.findByPublicIdAndDeletedIsFalse(reserveId)
                .orElseThrow(() -> new ResourceNotFoundException("Reserve not found"));
        
        // Verificar se o usuário pode liberar esta reserva
        if (!reserve.getUser().getPublicId().equals(userId)) {
            throw new IllegalStateException("User can only release their own reservations");
        }
        
        if (!reserve.getActive()) {
            throw new IllegalStateException("Reserve is already inactive");
        }
        
        // Finalizar reserva
        reserve.finishReserve();
        reserveRepository.save(reserve);
        
        // Liberar recurso
        reserve.getIotResource().updateStatus(IotResourceStatus.FREE);
        resourceRepository.save(reserve.getIotResource());
    }
    
    public List<ReserveDto> getActiveReservesByUser(UUID userId) {
        UserEntity user = userRepository.findByPublicIdAndDeletedIsFalse(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Instant now = Instant.now();
        return reserveRepository.findByUserIdAndActiveIsTrueAndDeletedIsFalse(user.getId())
                .stream()
                .filter(reserve -> reserve.getEndTime() == null && 
                                 (reserve.getPredictedEndTime() == null || reserve.getPredictedEndTime().isAfter(now)))
                .map(ReserveDto::fromEntity)
                .toList();
    }
    
    public Page<ReserveDto> getReserveHistory(UUID userId, Pageable pageable) {
        UserEntity user = userRepository.findByPublicIdAndDeletedIsFalse(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        return reserveRepository.findByUserIdAndDeletedIsFalseOrderByCreatedAtDesc(user.getId(), pageable)
                .map(ReserveDto::fromEntity);
    }
    
    public Page<ReserveDto> getAllReservesHistory(Pageable pageable) {
        return reserveRepository.findAllByDeletedIsFalseOrderByCreatedAtDesc(pageable)
                .map(ReserveDto::fromEntity);
    }
    
    public List<ReserveDto> getReservesByResource(UUID resourceId) {
        IotResourceEntity resource = resourceRepository.findByPublicIdAndDeletedIsFalse(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found"));
        
        return reserveRepository.findByIotResourceIdAndActiveIsTrueAndDeletedIsFalse(resource.getId())
                .stream()
                .map(ReserveDto::fromEntity)
                .toList();
    }
    
    @Transactional
    public void handleExpiredReserves() {
        List<IotResourceReserveEntity> expiredReserves = reserveRepository.findExpiredActiveReserves(Instant.now());
        
        for (IotResourceReserveEntity reserve : expiredReserves) {
            // Finalizar reserva
            reserve.finishReserve();
            reserveRepository.save(reserve);
            
            // Liberar recurso
            reserve.getIotResource().updateStatus(IotResourceStatus.FREE);
            resourceRepository.save(reserve.getIotResource());
        }
    }
}