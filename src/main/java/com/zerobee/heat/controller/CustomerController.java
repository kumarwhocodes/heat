package com.zerobee.heat.controller;

import com.zerobee.heat.dto.CustomResponse;
import com.zerobee.heat.dto.CustomerDTO;
import com.zerobee.heat.dto.ItineraryDto;
import com.zerobee.heat.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {
    
    private final CustomerService customerService;
    
    @GetMapping
    public CustomResponse<List<CustomerDTO>> getAllCustomers() {
        return new CustomResponse<>(HttpStatus.OK, "All customer Fetched", customerService.getAllCustomer());
    }
    
    @GetMapping("/{id}")
    public CustomResponse<CustomerDTO> getCustomer(@PathVariable String id) {
        return new CustomResponse<>(HttpStatus.OK, "Customer Fetched", customerService.getCustomerById(id));
    }
    
    @PostMapping
    public CustomResponse<CustomerDTO> createCustomer(@Valid @RequestBody CustomerDTO customerDTO) {
        return new CustomResponse<>(HttpStatus.CREATED, "Customer Created", customerService.createCustomer(customerDTO));
    }
    
    @PutMapping("/{id}")
    public CustomResponse<CustomerDTO> updateCustomer(@PathVariable String id, @RequestBody CustomerDTO customerDTO) {
        return new CustomResponse<>(HttpStatus.OK, "Customer Updated", customerService.updateCustomer(id, customerDTO));
    }
    
    @DeleteMapping("/{id}")
    public CustomResponse<Void> deleteCustomer(@PathVariable String id) {
        customerService.deleteCustomer(id);
        return new CustomResponse<>(HttpStatus.NO_CONTENT, "Customer Deleted", null);
    }

    @PostMapping("/itinerary/{id}")
    public CustomResponse<ItineraryDto> addItinerary(@PathVariable String id, @RequestBody ItineraryDto itineraryDto) {
        return new CustomResponse<>(HttpStatus.OK,"Itinerary Added",customerService.addItinerary(id,itineraryDto));
    }

    @GetMapping("/itinerary/{itineraryId}")
    public CustomResponse<ItineraryDto> getItinerary(@PathVariable UUID itineraryId) {
        return new CustomResponse<>(HttpStatus.OK, "Itinerary fetched", customerService.getItineraryById(itineraryId));
    }

    @PutMapping("/itinerary/{itineraryId}")
    public CustomResponse<ItineraryDto> updateItinerary(@PathVariable UUID itineraryId, @RequestBody ItineraryDto dto) {
        return new CustomResponse<>(HttpStatus.OK, "Itinerary updated", customerService.updateItinerary(itineraryId, dto));
    }

    @DeleteMapping("/itinerary/{itineraryId}")
    public CustomResponse<String> deleteItinerary(@PathVariable UUID itineraryId) {
        customerService.deleteItinerary(itineraryId);
        return new CustomResponse<>(HttpStatus.OK, "Itinerary deleted successfully", null);
    }
}
