package com.zerobee.heat.service;

import com.zerobee.heat.dto.VehicleDTO;
import com.zerobee.heat.entity.Vehicle;
import com.zerobee.heat.exception.ResourceNotFoundException;
import com.zerobee.heat.mapper.VehicleMapper;
import com.zerobee.heat.repo.VehicleRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepo vehicleRepo;
    private final VehicleMapper vehicleMapper;

    public List<VehicleDTO> getAllVehicles() {
        return vehicleRepo.findAll().stream()
                .map(vehicleMapper::toDTO)
                .collect(Collectors.toList());
    }

    public VehicleDTO getVehicleById(String regNo) {
        Vehicle vehicle = vehicleRepo.findById(regNo)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with registration no: " + regNo));
        return vehicleMapper.toDTO(vehicle);
    }

    @Transactional
    public VehicleDTO addVehicle(VehicleDTO vehicleDTO) {
        // Optional: Check for duplicate reg no before saving
        if (vehicleRepo.existsById(vehicleDTO.getVehicle_reg_no())) {
            throw new IllegalArgumentException("Vehicle registration number already exists.");
        }
        Vehicle vehicle = vehicleMapper.toEntity(vehicleDTO);
        Vehicle savedVehicle = vehicleRepo.save(vehicle);
        return vehicleMapper.toDTO(savedVehicle);
    }

    @Transactional
    public VehicleDTO updateVehicle(String regNo, VehicleDTO vehicleDTO) {
        Vehicle existingVehicle = vehicleRepo.findById(regNo)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with registration no: " + regNo));

        // Since regNo is the ID, you can't change it - editing the PK is not supported
        if (vehicleDTO.getName() != null) existingVehicle.setName(vehicleDTO.getName());
        if (vehicleDTO.getCategory() != null) existingVehicle.setCategory(vehicleDTO.getCategory());
        if (vehicleDTO.getGrades() != null) existingVehicle.setGrades(vehicleDTO.getGrades());
        if (vehicleDTO.getPollution_end_date() != null) existingVehicle.setPollution_end_date(vehicleDTO.getPollution_end_date());
        if (vehicleDTO.getInsurance_end_date() != null) existingVehicle.setInsurance_end_date(vehicleDTO.getInsurance_end_date());
        if (vehicleDTO.getBooking_start_date() != null) existingVehicle.setBooking_start_date(vehicleDTO.getBooking_start_date());
        if (vehicleDTO.getBooking_end_date() != null) existingVehicle.setBooking_end_date(vehicleDTO.getBooking_end_date());
        if (vehicleDTO.getChallan_check() != null) existingVehicle.setChallan_check(vehicleDTO.getChallan_check());
        if (vehicleDTO.getAvailability() != null) existingVehicle.setAvailability(vehicleDTO.getAvailability());

        Vehicle updatedVehicle = vehicleRepo.save(existingVehicle);
        return vehicleMapper.toDTO(updatedVehicle);
    }

    @Transactional
    public VehicleDTO deleteVehicle(String regNo) {
        Vehicle vehicle = vehicleRepo.findById(regNo)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with registration no: " + regNo));
        vehicleRepo.delete(vehicle);
        return vehicleMapper.toDTO(vehicle);
    }
}
