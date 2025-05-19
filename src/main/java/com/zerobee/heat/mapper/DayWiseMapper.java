package com.zerobee.heat.mapper;

import com.zerobee.heat.dto.DayWiseDto;
import com.zerobee.heat.entity.DayWise;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DayWiseMapper {

    DayWiseDto toDTO(DayWise dayWise);

    @Mapping(target = "itinerary", ignore = true)
    DayWise toEntity(DayWiseDto dayWiseDto);
}
