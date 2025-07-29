package com.zerobee.heat.controller;

import com.zerobee.heat.dto.CustomResponse;
import com.zerobee.heat.dto.VehicleDTO;
import com.zerobee.heat.service.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    @GetMapping("/")
    public CustomResponse<List<VehicleDTO>> getAllVehicles() {
        return new CustomResponse<>(HttpStatus.OK, "All Vehicles fetched.", vehicleService.getAllVehicles());
    }

    @GetMapping("/{vehicleRegNo}")
    public CustomResponse<VehicleDTO> getVehicleById(@PathVariable String vehicleRegNo) {
        return new CustomResponse<>(
                HttpStatus.OK,
                "Vehicle By Registration Number: " + vehicleRegNo + " Fetched",
                vehicleService.getVehicleById(vehicleRegNo)
        );
    }

    @PostMapping("/")
    public CustomResponse<VehicleDTO> addVehicle(@Valid @RequestBody VehicleDTO vehicleDTO) {
        return new CustomResponse<>(HttpStatus.CREATED, "Vehicle Added", vehicleService.addVehicle(vehicleDTO));
    }

    @PutMapping("/{vehicleRegNo}")
    public CustomResponse<VehicleDTO> updateVehicle(@PathVariable String vehicleRegNo, @RequestBody VehicleDTO vehicleDTO) {
        return new CustomResponse<>(HttpStatus.OK, "Vehicle Updated", vehicleService.updateVehicle(vehicleRegNo, vehicleDTO));
    }

    @DeleteMapping("/{vehicleRegNo}")
    public CustomResponse<VehicleDTO> deleteVehicle(@PathVariable String vehicleRegNo) {
        return new CustomResponse<>(HttpStatus.NO_CONTENT, "Vehicle Removed.", vehicleService.deleteVehicle(vehicleRegNo));
    }
}
