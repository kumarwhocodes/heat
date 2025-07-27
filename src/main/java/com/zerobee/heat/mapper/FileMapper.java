package com.zerobee.heat.mapper;

import com.zerobee.heat.dto.FileDTO;
import com.zerobee.heat.dto.CreateFileRequestDTO;
import com.zerobee.heat.entity.File;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {CustomerMapper.class, ItineraryMapper.class})
public interface FileMapper {
    
    /* ENTITY → DTO (For Response) */
    @Mapping(target = "customer", source = "customer")
    @Mapping(target = "itinerary", source = "itinerary")
    FileDTO toDTO(File file);
    
    /* CreateFileRequestDTO → ENTITY (For Creation) */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "itinerary", ignore = true)
    File toEntity(CreateFileRequestDTO request);
    
    /* Alternative: If you still need FileDTO → ENTITY mapping */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "itinerary", ignore = true)
    @Mapping(target = "customerId", ignore = true)    // Set in service
    @Mapping(target = "itineraryId", ignore = true)   // Set in service
    File toEntityFromDTO(FileDTO dto);
}
