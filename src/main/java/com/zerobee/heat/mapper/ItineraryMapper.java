package com.zerobee.heat.mapper;

import com.zerobee.heat.dto.DayWiseDto;
import com.zerobee.heat.dto.ItineraryDto;
import com.zerobee.heat.entity.DayWise;
import com.zerobee.heat.entity.Itinerary;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",uses = DayWiseMapper.class)
public interface ItineraryMapper {

    ItineraryDto toDTO(Itinerary itinerary);

    @Mapping(target = "customer", ignore = true)
    Itinerary toEntity(ItineraryDto itineraryDto);
}
