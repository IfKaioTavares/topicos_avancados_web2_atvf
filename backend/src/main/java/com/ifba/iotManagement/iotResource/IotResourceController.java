package com.ifba.iotManagement.iotResource;

import com.ifba.iotManagement.iotResource.dto.CreateIotResourceRequest;
import com.ifba.iotManagement.iotResource.dto.IotResourceDto;
import com.ifba.iotManagement.shared.BaseController;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/iot-resources")
public class IotResourceController extends BaseController {
    private final IotResourceService iotResourceService;
    public IotResourceController(IotResourceService iotResourceService) {
        this.iotResourceService = iotResourceService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> createIotResource(@RequestBody CreateIotResourceRequest request) {
        iotResourceService.save(request);
        return ResponseEntity.ok().build();
    }


    public ResponseEntity<List<IotResourceDto>> findAll() {
        return ResponseEntity.ok(iotResourceService.findAll());
    }


}
