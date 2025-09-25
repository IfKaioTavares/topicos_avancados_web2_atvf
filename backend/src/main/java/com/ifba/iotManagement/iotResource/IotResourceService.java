package com.ifba.iotManagement.iotResource;

import com.ifba.iotManagement.iotResource.dto.CreateIotResourceRequest;
import com.ifba.iotManagement.iotResource.dto.IotResourceDto;
import com.ifba.iotManagement.shared.exceptions.ResourceAlreadyExistsException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IotResourceService {
    private final IotResourceRepository iotResourceRepository;
    public IotResourceService(IotResourceRepository iotResourceRepository) {
        this.iotResourceRepository = iotResourceRepository;
    }

    public void save(CreateIotResourceRequest request) {
        iotResourceRepository.findByResourceIdAndDeletedIsFalse(request.resourceId()).ifPresent(iotResource -> {
            throw new ResourceAlreadyExistsException("Iot com resourceId " + request.resourceId() + " já existe");
        });
        iotResourceRepository.save(CreateIotResourceRequest.toEntity(request));
    }

    public List<IotResourceDto> findAll() {
        return iotResourceRepository.findAllByDeletedIsFalseAndLockedForAdminIsFalse().stream()
                .map(IotResourceDto::fromEntity)
                .toList();
    }
}
