package com.zerobee.heat.mapper;

import com.zerobee.heat.dto.ItineraryDTO;
import com.zerobee.heat.entity.Itinerary;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",uses = DayWiseMapper.class)
public interface ItineraryMapper {

    ItineraryDTO toDTO(Itinerary itinerary);

    @Mapping(target = "customer", ignore = true)
    Itinerary toEntity(ItineraryDTO itineraryDto);
}
