package com.zerobee.heat.controller;

import com.zerobee.heat.dto.CustomResponse;
import com.zerobee.heat.dto.VehicleDTO;
import com.zerobee.heat.service.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    @GetMapping("/")
    public CustomResponse<List<VehicleDTO>> getAllVehicles() {
        return new CustomResponse<>(HttpStatus.OK, "All Vehiches fetched.", vehicleService.getAllVehicles());
    }

    @GetMapping("/{id}")
    public CustomResponse<VehicleDTO> getVehicleById(@PathVariable UUID id) {
        return new CustomResponse<>(
                HttpStatus.OK,
                "Vehicle By Id" + id + "Fetched",
                vehicleService.getVehicleById(id)
        );
    }

    @PostMapping("/")
    public CustomResponse<VehicleDTO> addVehicle(@Valid @RequestBody VehicleDTO vehicleDTO) {
        return new CustomResponse<>(HttpStatus.CREATED, "Vehicle Added", vehicleService.addVehicle(vehicleDTO));
    }

    @PutMapping("/{id}")
    public CustomResponse<VehicleDTO> updateVehicle(@PathVariable String id, @RequestBody VehicleDTO vehicleDTO) {
        return new CustomResponse<>(HttpStatus.OK, "Vehicle Updated", vehicleService.updateVehicle(id, vehicleDTO));
    }

    @DeleteMapping("/{id}")
    public CustomResponse<VehicleDTO> deleteVehicle(@PathVariable String id) {
        return new CustomResponse<>(HttpStatus.NO_CONTENT, "Vehicle Removed.", vehicleService.deleteVehicle(id));
    }
}
