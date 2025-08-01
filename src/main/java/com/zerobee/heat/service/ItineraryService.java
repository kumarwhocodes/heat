package com.zerobee.heat.service;

import com.zerobee.heat.dto.ItineraryDTO;
import com.zerobee.heat.entity.Customer;
import com.zerobee.heat.entity.DayWise;
import com.zerobee.heat.entity.Itinerary;
import com.zerobee.heat.enums.FileStatus;
import com.zerobee.heat.exception.ResourceNotFoundException;
import com.zerobee.heat.mapper.DayWiseMapper;
import com.zerobee.heat.mapper.ItineraryMapper;
import com.zerobee.heat.repo.CustomerRepo;
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
public class ItineraryService {
    
    private final ItineraryRepo itineraryRepo;
    private final CustomerRepo customerRepo;
    private final DayWiseRepo dayWiseRepo;
    private final ItineraryMapper itineraryMapper;
    private final DayWiseMapper dayWiseMapper;
    
    public List<ItineraryDTO> getAllItineraries() {
        return itineraryRepo.findAll().stream()
                .map(itineraryMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    public ItineraryDTO getItineraryById(UUID itineraryId) {
        Itinerary itinerary = itineraryRepo.findById(itineraryId)
                .orElseThrow(() -> new ResourceNotFoundException("Itinerary not found with id: " + itineraryId));
        return itineraryMapper.toDTO(itinerary);
    }
    
    public List<ItineraryDTO> getItinerariesByCustomerId(String customerId) {
        if (!customerRepo.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer not found with id: " + customerId);
        }
        
        return itineraryRepo.findByCustomer_CustomerId(customerId).stream()
                .map(itineraryMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public ItineraryDTO createItinerary(String customerId, ItineraryDTO itineraryDto) {
        // Get the customer
        Customer customer = customerRepo.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));
        
        // Convert DTO to entity
        Itinerary itinerary = itineraryMapper.toEntity(itineraryDto);
        
        // Set customer reference
        itinerary.setCustomer(customer);
        
        // Save the itinerary
        Itinerary savedItinerary = itineraryRepo.save(itinerary);
        
        // Add the itinerary to the customer's collection (using helper method)
        customer.addItinerary(savedItinerary);
        
        return itineraryMapper.toDTO(savedItinerary);
    }
    
    @Transactional
    public ItineraryDTO updateItinerary(UUID itineraryId, ItineraryDTO updatedDto) {
        // Get the existing itinerary
        Itinerary itinerary = itineraryRepo.findById(itineraryId)
                .orElseThrow(() -> new ResourceNotFoundException("Itinerary not found with id: " + itineraryId));
        
        // Update fields
        if (updatedDto.getAgentName() != null) itinerary.setAgentName(updatedDto.getAgentName());
        if (updatedDto.getAgentEmail() != null) itinerary.setAgentEmail(updatedDto.getAgentEmail());
        if (updatedDto.getAgentPhone() != null) itinerary.setAgentPhone(updatedDto.getAgentPhone());
        if (updatedDto.getNumAdult() != null) itinerary.setNumAdult(updatedDto.getNumAdult());
        if (updatedDto.getNumChildren() != null) itinerary.setNumChildren(updatedDto.getNumChildren());
        if (updatedDto.getTourCode() != null) itinerary.setTourCode(updatedDto.getTourCode());
        if (updatedDto.getDestination() != null) itinerary.setDestination(updatedDto.getDestination());
        if (updatedDto.getDuration() != null) itinerary.setDuration(updatedDto.getDuration());
        if (updatedDto.getStartDate() != null) itinerary.setStartDate(updatedDto.getStartDate());
        if (updatedDto.getEndDate() != null) itinerary.setEndDate(updatedDto.getEndDate());
        if (updatedDto.getNoOfDays() != null) itinerary.setNoOfDays(updatedDto.getNoOfDays());
        if (updatedDto.getNoOfNights() != null) itinerary.setNoOfNights(updatedDto.getNoOfNights());
        if (updatedDto.getArrival() != null) itinerary.setArrival(updatedDto.getArrival());
        
        // Save updated itinerary
        Itinerary saved = itineraryRepo.save(itinerary);
        
        return itineraryMapper.toDTO(saved);
    }
    
    @Transactional
    public void deleteItinerary(UUID itineraryId) {
        if (!itineraryRepo.existsById(itineraryId)) {
            throw new ResourceNotFoundException("Itinerary not found with id: " + itineraryId);
        }
        
        // The cascading delete will take care of related day-wise records
        itineraryRepo.deleteById(itineraryId);
    }
}