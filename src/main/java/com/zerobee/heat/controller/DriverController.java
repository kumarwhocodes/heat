package com.zerobee.heat.controller;

import com.zerobee.heat.dto.CustomResponse;
import com.zerobee.heat.dto.DriverDTO;
import com.zerobee.heat.service.DriverService;
import org.springframework.web.bind.annotation.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/driver")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService driverService;

    @GetMapping("/")
    public CustomResponse<List<DriverDTO>> getAllDriver() {
        return new CustomResponse<>(
                HttpStatus.OK,
                "Fetched All Driver",
                driverService.getAll()
        );
    }

    @GetMapping("/{id}")
    public CustomResponse<DriverDTO> getDriver(@PathVariable UUID id) {
        return new CustomResponse<>(
                HttpStatus.OK,
                "Fetched Driver Detail by:" + id,
                driverService.getById(id)
        );
    }

    @PostMapping("/")
    public CustomResponse<DriverDTO> createDriver(
            @RequestBody DriverDTO dto
    ) {
        return new CustomResponse<>(
                HttpStatus.CREATED,
                "Created Driver",
                driverService.createDriver(dto)
        );
    }

    @PutMapping("/{id}")
    public CustomResponse<DriverDTO> updateDriver(
            @PathVariable UUID id,
            @RequestBody DriverDTO dto
    ) {
        return new CustomResponse<>(
                HttpStatus.OK,
                "Updated Driver detail by id: " + dto.getId(),
                driverService.updateDriver(id,dto)
        );
    }

    @DeleteMapping("/{id}")
    public CustomResponse<Void> deleteDriver(@PathVariable UUID id) {
        driverService.deleteDriverById(id);
        return new CustomResponse<>(
                HttpStatus.OK,
                "Deleted Driver with Id: "+ id,
                null
        );
    }

}
