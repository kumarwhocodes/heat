package com.zerobee.heat.mapper;

import com.zerobee.heat.dto.DriverDTO;
import com.zerobee.heat.entity.Driver;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DriverMapper {

    DriverDTO toDTO(Driver driver);

    Driver toEntity(DriverDTO driverDTO);

}