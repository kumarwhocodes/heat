package com.zerobee.heat.controller;

import com.zerobee.heat.dto.CustomResponse;
import com.zerobee.heat.dto.ItineraryDTO;
import com.zerobee.heat.service.ItineraryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/itineraries")
@RequiredArgsConstructor
public class ItineraryController {
    
    private final ItineraryService itineraryService;
    
    @GetMapping
    public CustomResponse<List<ItineraryDTO>> getAllItineraries() {
        return new CustomResponse<>(HttpStatus.OK, "All itineraries fetched", itineraryService.getAllItineraries());
    }
    
    @GetMapping("/{id}")
    public CustomResponse<ItineraryDTO> getItinerary(@PathVariable UUID id) {
        return new CustomResponse<>(HttpStatus.OK, "Itinerary fetched", itineraryService.getItineraryById(id));
    }
    
    @GetMapping("/customer/{customerId}")
    public CustomResponse<List<ItineraryDTO>> getItinerariesByCustomer(@PathVariable String customerId) {
        return new CustomResponse<>(HttpStatus.OK, "Customer itineraries fetched",
                itineraryService.getItinerariesByCustomerId(customerId));
    }
    
    @PostMapping("/customer/{customerId}")
    public CustomResponse<ItineraryDTO> createItinerary(@PathVariable String customerId,
                                                        @RequestBody ItineraryDTO itineraryDTO) {
        return new CustomResponse<>(HttpStatus.CREATED, "Itinerary created",
                itineraryService.createItinerary(customerId, itineraryDTO));
    }
    
    @PutMapping("/{id}")
    public CustomResponse<ItineraryDTO> updateItinerary(@PathVariable UUID id, @RequestBody ItineraryDTO itineraryDTO) {
        return new CustomResponse<>(HttpStatus.OK, "Itinerary updated",
                itineraryService.updateItinerary(id, itineraryDTO));
    }
    
    @DeleteMapping("/{id}")
    public CustomResponse<Void> deleteItinerary(@PathVariable UUID id) {
        itineraryService.deleteItinerary(id);
        return new CustomResponse<>(HttpStatus.NO_CONTENT, "Itinerary deleted", null);
    }
}