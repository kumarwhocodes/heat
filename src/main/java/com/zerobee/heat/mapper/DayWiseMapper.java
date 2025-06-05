package com.zerobee.heat.mapper;

import com.zerobee.heat.dto.DayWiseDTO;
import com.zerobee.heat.entity.DayWise;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DayWiseMapper {

    DayWiseDTO toDTO(DayWise dayWise);

    @Mapping(target = "itinerary", ignore = true)
    DayWise toEntity(DayWiseDTO dayWiseDto);
}
