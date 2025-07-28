package com.zerobee.heat.service;

import com.zerobee.heat.dto.DriverDTO;
import com.zerobee.heat.entity.Driver;
import com.zerobee.heat.entity.Vehicle;
import com.zerobee.heat.exception.ResourceNotFoundException;
import com.zerobee.heat.mapper.DriverMapper;
import com.zerobee.heat.mapper.VehicleMapper;
import com.zerobee.heat.repo.DriverRepo;
import com.zerobee.heat.repo.VehicleRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DriverService {

    private final DriverMapper driverMapper;
    private final VehicleMapper vehicleMapper;
    private final VehicleRepo vehicleRepo;
    private final DriverRepo repo;

    public List<DriverDTO> getAll() {
        return repo.findAll().stream()
                .map(driverMapper::toDTO)
                .collect(Collectors.toList());
    }

    public DriverDTO getById(UUID id) {
        Driver driver = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found"));
        return driverMapper.toDTO(driver);
    }

    public DriverDTO createDriver(DriverDTO dto) {
        System.out.println("DTO: " + dto);
        Driver driver = driverMapper.toEntity(dto);
        System.out.println("Mapped entity: " + driver.getVehicle().getId());
        if(driver.getVehicle().getId() == null){
            driver.setVehicle(null);
        }
//        if (dto.getVehicle() != null && dto.getVehicle().getId() != null) {
//            UUID vehicleId = UUID.fromString(dto.getVehicle().getId());
//            Vehicle vehicle = vehicleRepo.findById(vehicleId)
//                    .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with ID: " + vehicleId));
//            driver.setVehicle(vehicle);
//        }
        Driver saved = repo.save(driver);
        return driverMapper.toDTO(saved);
    }

    public DriverDTO updateDriver(UUID id, DriverDTO dto) {
        Driver driver = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found"));

        if (dto.getName() != null) driver.setName(dto.getName());
        if (dto.getEmail() != null) driver.setEmail(dto.getEmail());
        if (dto.getPhone() != null) driver.setPhone(dto.getPhone());
        if (dto.getLicenseNumber() != null) driver.setLicenseNumber(dto.getLicenseNumber());
        if (dto.getLicenseExpiryDate() != null) driver.setLicenseExpiryDate(dto.getLicenseExpiryDate());
        if (dto.getAddress() != null) driver.setAddress(dto.getAddress());
        if (dto.getExperienceYears() != null) driver.setExperienceYears(dto.getExperienceYears());
        if (dto.getIsAvailable() != null) driver.setIsAvailable(dto.getIsAvailable());
        if (dto.getGrades() != null) driver.setGrades(dto.getGrades());
        if (dto.getVehicle() != null) driver.setVehicle(vehicleMapper.toEntity(dto.getVehicle()));

        Driver saved = repo.save(driver);

        return driverMapper.toDTO(saved);
    }

    @Transactional
    public void deleteDriverById(UUID id) {
        if(!repo.existsById(id))
            throw new ResourceNotFoundException("Driver not found");

        repo.deleteById(id);
    }

}
