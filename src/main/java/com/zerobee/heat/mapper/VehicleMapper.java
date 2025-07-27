package com.zerobee.heat.mapper;

import com.zerobee.heat.dto.VehicleDTO;
import com.zerobee.heat.entity.Vehicle;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VehicleMapper {

    VehicleDTO toDTO(Vehicle vehicle);
    Vehicle toEntity(VehicleDTO vehicleDTO);
}
