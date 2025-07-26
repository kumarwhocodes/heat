package com.zerobee.heat.service;

import com.zerobee.heat.dto.CustomerDTO;
import com.zerobee.heat.entity.Customer;
import com.zerobee.heat.exception.ConflictException;
import com.zerobee.heat.exception.ResourceNotFoundException;
import com.zerobee.heat.mapper.CustomerMapper;
import com.zerobee.heat.repo.CustomerRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerService {
    
    private final CustomerRepo customerRepo;
    private final CustomerMapper customerMapper;
    
    public List<CustomerDTO> getAllCustomers() {
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
        if (customerDTO.getClientPhone() != null && customerRepo.existsByClientPhone(customerDTO.getClientPhone())) {
            throw new ConflictException("Customer with phone number already exists!");
        }
        
        if (customerDTO.getClientEmail() != null && customerRepo.existsByClientEmail(customerDTO.getClientEmail())) {
            throw new ConflictException("Customer with email id already exists!");
        }
        
        // Generate customer ID
        String newCustomerId = generateCustomerId();
        customerDTO.setCustomerId(newCustomerId);
        
        Customer customer = customerMapper.toEntity(customerDTO);
        Customer savedCustomer = customerRepo.save(customer);
        
        return customerMapper.toDTO(savedCustomer);
    }
    
    public CustomerDTO updateCustomer(String id, CustomerDTO customerDTO) {
        Customer existingCustomer = customerRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        
        if (customerDTO.getClientPhone() != null &&
                !customerDTO.getClientPhone().equals(existingCustomer.getClientPhone()) &&
                customerRepo.existsByClientPhone(customerDTO.getClientPhone())) {
            throw new ConflictException("Customer with phone number already exists!");
        }
        
        if (customerDTO.getClientEmail() != null &&
                !customerDTO.getClientEmail().equals(existingCustomer.getClientEmail()) &&
                customerRepo.existsByClientEmail(customerDTO.getClientEmail())) {
            throw new ConflictException("Customer with email id already exists!");
        }
        
        if (customerDTO.getClientName() != null) existingCustomer.setClientName(customerDTO.getClientName());
        if (customerDTO.getClientEmail() != null) existingCustomer.setClientEmail(customerDTO.getClientEmail());
        if (customerDTO.getClientPhone() != null) existingCustomer.setClientPhone(customerDTO.getClientPhone());
        if (customerDTO.getNationality() != null) existingCustomer.setNationality(customerDTO.getNationality());
        if (customerDTO.getClientAlternatePhone() != null)
            existingCustomer.setClientAlternatePhone(customerDTO.getClientAlternatePhone());
        if (customerDTO.getClientLanguage() != null)
            existingCustomer.setClientLanguage(customerDTO.getClientLanguage());
        
        Customer updatedCustomer = customerRepo.save(existingCustomer);
        return customerMapper.toDTO(updatedCustomer);
    }
    
    @Transactional
    public void deleteCustomer(String id) {
        if (!customerRepo.existsById(id)) {
            throw new ResourceNotFoundException("Customer not found with id: " + id);
        }
        customerRepo.deleteById(id);
    }
}