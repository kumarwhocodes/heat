package com.zerobee.heat.service;

import com.zerobee.heat.dto.CustomerDTO;
import com.zerobee.heat.dto.DayWiseDto;
import com.zerobee.heat.dto.ItineraryDto;
import com.zerobee.heat.entity.Customer;
import com.zerobee.heat.entity.DayWise;
import com.zerobee.heat.entity.Itinerary;
import com.zerobee.heat.exception.ConflictException;
import com.zerobee.heat.exception.ResourceNotFoundException;
import com.zerobee.heat.mapper.CustomerMapper;
import com.zerobee.heat.mapper.DayWiseMapper;
import com.zerobee.heat.mapper.ItineraryMapper;
import com.zerobee.heat.repo.CustomerRepo;
import com.zerobee.heat.repo.DayWiseRepo;
import com.zerobee.heat.repo.ItineraryRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepo customerRepo;
    private final CustomerMapper customerMapper;
    private final ItineraryRepo itineraryRepo;
    private final DayWiseRepo dayWiseRepo;
    private final ItineraryMapper itineraryMapper;
    private final DayWiseMapper dayWiseMapper;

    public List<CustomerDTO> getAllCustomer() {
        List<Customer> customers = customerRepo.findAll();
        return customers.stream()
                .map(customerMapper::toDTO)
                .collect(Collectors.toList());
    }

    public CustomerDTO getCustomerById(String id) {
        Customer customer = customerRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        return customerMapper.toDTO(customer);
    }

    private String generateCustomerId() {
        Optional<Customer> lastCustomer = customerRepo
                .findTopByCustomerIdStartingWithOrderByCustomerIdDesc("CUST_");

        int nextNumber = 1;
        if (lastCustomer.isPresent()) {
            String lastId = lastCustomer.get().getCustomerId();
            String numericPart = lastId.substring("CUST_".length());
            nextNumber = Integer.parseInt(numericPart) + 1;
        }

        return "CUST_" + String.format("%03d", nextNumber);
    }

    @Transactional
    public CustomerDTO createCustomer(CustomerDTO customerDTO) {
        if (customerRepo.existsByClientPhone(customerDTO.getClientPhone())) {
            throw new ConflictException("Customer with phone number already exists!");
        }

        if (customerRepo.existsByClientEmail(customerDTO.getClientEmail())) {
            throw new ConflictException("Customer with email id already exists!");
        }

        String newCustomerId = generateCustomerId();
        customerDTO.setCustomerId(newCustomerId);

        Customer customer = customerMapper.toEntity(customerDTO);

        if (customer.getItineraries() == null) {
            customer.setItineraries(new ArrayList<>());
        }

        Customer savedCustomer = customerRepo.save(customer);

        if (customerDTO.getItineraries() != null && !customerDTO.getItineraries().isEmpty()) {
            for (ItineraryDto itineraryDTO : customerDTO.getItineraries()) {
                Itinerary itinerary = itineraryMapper.toEntity(itineraryDTO);
                if (itinerary.getDayWiseList() == null) {
                    itinerary.setDayWiseList(new ArrayList<>());
                }
                itinerary.setCustomer(savedCustomer);
                Itinerary savedItinerary = itineraryRepo.save(itinerary);

                if(itineraryDTO.getDayWiseList() != null && !itineraryDTO.getDayWiseList().isEmpty()) {
                    for (DayWiseDto dayWiseDto : itineraryDTO.getDayWiseList()) {
                        DayWise dayWise = dayWiseMapper.toEntity(dayWiseDto);
                        dayWise.setItinerary(savedItinerary);
                        dayWiseRepo.save(dayWise);
                    }
                }
            }
        }

        return customerMapper.toDTO(savedCustomer);
    }

    public CustomerDTO updateCustomer(String id, CustomerDTO customerDTO) {

        Customer existingCustomer = customerRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));

        if (customerDTO.getClientName() != null) existingCustomer.setClientName(customerDTO.getClientName());
        if (customerDTO.getClientEmail() != null) existingCustomer.setClientEmail(customerDTO.getClientEmail());
        if (customerDTO.getClientPhone() != null) existingCustomer.setClientPhone(customerDTO.getClientPhone());
        Customer updatedCustomer = customerRepo.save(existingCustomer);

        return customerMapper.toDTO(updatedCustomer);
    }

    public void deleteCustomer(String id) {

        if (!customerRepo.existsById(id)) {
            throw new ResourceNotFoundException("Customer not found with id: " + id);
        }
        customerRepo.deleteById(id);
    }

    public ItineraryDto addItinerary(String customerId, ItineraryDto itineraryDto) {
        Customer customer = customerRepo.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));

        Itinerary itinerary = itineraryMapper.toEntity(itineraryDto);
        itinerary.setCustomer(customer);

        if (itinerary.getDayWiseList() == null) {
            itinerary.setDayWiseList(new ArrayList<>());
        }

        Itinerary savedItinerary = itineraryRepo.save(itinerary);

        if (itineraryDto.getDayWiseList() != null && !itineraryDto.getDayWiseList().isEmpty()) {
            for (DayWiseDto dayWiseDto : itineraryDto.getDayWiseList()) {
                DayWise dayWise = dayWiseMapper.toEntity(dayWiseDto);
                dayWise.setItinerary(savedItinerary);
                dayWiseRepo.save(dayWise);
            }
        }

        return itineraryMapper.toDTO(savedItinerary);
    }

    public ItineraryDto getItineraryById(String customerId, UUID itineraryId) {
        Customer customer = customerRepo.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));

        Itinerary itinerary = itineraryRepo.findById(itineraryId)
                .orElseThrow(() -> new ResourceNotFoundException("Itinerary not found with id: " + itineraryId));

        if (!itinerary.getCustomer().getCustomerId().equals(customer.getCustomerId())) {
            throw new ConflictException("Itinerary does not belong to the specified customer");
        }

        return itineraryMapper.toDTO(itinerary);
    }

    @Transactional
    public ItineraryDto updateItinerary(String customerId, UUID itineraryId, ItineraryDto updatedDto) {
        Customer customer = customerRepo.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));

        Itinerary itinerary = itineraryRepo.findById(itineraryId)
                .orElseThrow(() -> new ResourceNotFoundException("Itinerary not found with id: " + itineraryId));

        if (!itinerary.getCustomer().getCustomerId().equals(customer.getCustomerId())) {
            throw new ConflictException("Itinerary does not belong to the specified customer");
        }

        // Update fields
        itinerary.setAgentName(updatedDto.getAgentName());
        itinerary.setAgentEmail(updatedDto.getAgentEmail());
        itinerary.setAgentPhone(updatedDto.getAgentPhone());
        itinerary.setNumAdult(updatedDto.getNumAdult());
        itinerary.setNumChildren(updatedDto.getNumChildren());
        itinerary.setTourCode(updatedDto.getTourCode());
        itinerary.setDestination(updatedDto.getDestination());
        itinerary.setDuration(updatedDto.getDuration());
        itinerary.setStartDate(updatedDto.getStartDate());
        itinerary.setEndDate(updatedDto.getEndDate());
        itinerary.setNoOfDays(updatedDto.getNoOfDays());
        itinerary.setNoOfNights(updatedDto.getNoOfNights());
        itinerary.setArrival(updatedDto.getArrival());

        Itinerary saved = itineraryRepo.save(itinerary);

        return itineraryMapper.toDTO(saved);
    }

    @Transactional
    public void deleteItinerary(String customerId, UUID itineraryId) {
        Customer customer = customerRepo.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));

        Itinerary itinerary = itineraryRepo.findById(itineraryId)
                .orElseThrow(() -> new ResourceNotFoundException("Itinerary not found with id: " + itineraryId));

        if (!itinerary.getCustomer().getCustomerId().equals(customer.getCustomerId())) {
            throw new ConflictException("Itinerary does not belong to the specified customer");
        }

        // Delete related DayWise records
        List<DayWise> dayWiseList = dayWiseRepo.findByItinerary_ItineraryId(itineraryId);
        dayWiseRepo.deleteAll(dayWiseList);

        itineraryRepo.delete(itinerary);
    }
}
