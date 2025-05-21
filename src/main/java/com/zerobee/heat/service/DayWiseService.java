package com.zerobee.heat.service;

import com.zerobee.heat.dto.DayWiseDTO;
import com.zerobee.heat.entity.DayWise;
import com.zerobee.heat.entity.Itinerary;
import com.zerobee.heat.exception.ResourceNotFoundException;
import com.zerobee.heat.mapper.DayWiseMapper;
import com.zerobee.heat.repo.DayWiseRepo;
import com.zerobee.heat.repo.ItineraryRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DayWiseService {
    
    private final DayWiseRepo dayWiseRepo;
    private final ItineraryRepo itineraryRepo;
    private final DayWiseMapper dayWiseMapper;
    
    public DayWiseDTO getDayWiseById(UUID id) {
        DayWise dayWise = dayWiseRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DayWise not found with id: " + id));
        return dayWiseMapper.toDTO(dayWise);
    }
    
    public List<DayWiseDTO> getDayWiseByItinerary(UUID itineraryId) {
        if (!itineraryRepo.existsById(itineraryId)) {
            throw new ResourceNotFoundException("Itinerary not found with id: " + itineraryId);
        }
        
        return dayWiseRepo.findByItinerary_ItineraryId(itineraryId).stream()
                .map(dayWiseMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public DayWiseDTO createDayWise(UUID itineraryId, DayWiseDTO dayWiseDto) {
        // Get the itinerary
        Itinerary itinerary = itineraryRepo.findById(itineraryId)
                .orElseThrow(() -> new ResourceNotFoundException("Itinerary not found with id: " + itineraryId));
        
        // Convert DTO to entity
        DayWise dayWise = dayWiseMapper.toEntity(dayWiseDto);
        
        // Set itinerary reference
        dayWise.setItinerary(itinerary);
        
        // Save the day-wise entity
        DayWise savedDayWise = dayWiseRepo.save(dayWise);
        
        // Add day-wise to itinerary's collection using helper method
        itinerary.addDayWise(savedDayWise);
        
        return dayWiseMapper.toDTO(savedDayWise);
    }
    
    @Transactional
    public DayWiseDTO updateDayWise(UUID id, DayWiseDTO dayWiseDto) {
        // Get the existing day-wise entity
        DayWise dayWise = dayWiseRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DayWise not found with id: " + id));
        
        // Update fields
        if (dayWiseDto.getDate() != null) dayWise.setDate(dayWiseDto.getDate());
        if (dayWiseDto.getDay() != null) dayWise.setDay(dayWiseDto.getDay());
        if (dayWiseDto.getDestination() != null) dayWise.setDestination(dayWiseDto.getDestination());
        if (dayWiseDto.getNotes() != null) dayWise.setNotes(dayWiseDto.getNotes());
        
        // Save updated entity
        DayWise updatedDayWise = dayWiseRepo.save(dayWise);
        
        return dayWiseMapper.toDTO(updatedDayWise);
    }
    
    @Transactional
    public void deleteDayWise(UUID id) {
        if (!dayWiseRepo.existsById(id)) {
            throw new ResourceNotFoundException("DayWise not found with id: " + id);
        }
        
        dayWiseRepo.deleteById(id);
    }
}