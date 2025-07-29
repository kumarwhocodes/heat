package com.zerobee.heat.mapper;

import com.zerobee.heat.dto.FileDTO;
import com.zerobee.heat.entity.File;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {CustomerMapper.class, ItineraryMapper.class})
public interface FileMapper {
    
    @Mapping(target = "customer", source = "customer")
    @Mapping(target = "itinerary", source = "itinerary")
    FileDTO toDTO(File file);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "itinerary", ignore = true)
    File toEntity(FileDTO fileDTO);
    
}
