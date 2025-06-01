package com.zerobee.heat.mapper;

import com.zerobee.heat.dto.PlaceDTO;
import com.zerobee.heat.entity.Place;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = ItineraryMapper.class)
public interface PlaceMapper {
    
    PlaceDTO toDTO(Place place);
    
    Place toEntity(PlaceDTO placeDTO);
    
}