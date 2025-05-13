package com.zerobee.heat.service;

import com.zerobee.heat.dto.CustomerDTO;
import com.zerobee.heat.entity.Customer;
import com.zerobee.heat.exception.ConflictException;
import com.zerobee.heat.exception.ResourceNotFoundException;
import com.zerobee.heat.mapper.CustomerMapper;
import com.zerobee.heat.repo.CustomerRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerService {
    
    private final CustomerRepo customerRepo;
    private final CustomerMapper customerMapper;
    
    public List<CustomerDTO> getAllCustomer() {
        List<Customer> customers = customerRepo.findAll();
        return customers.stream()
                .map(customerMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    public CustomerDTO getCustomerById(String id) {
        Customer customer = customerRepo.findById(UUID.fromString(id))
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        return customerMapper.toDTO(customer);
    }
    
    public CustomerDTO createCustomer(CustomerDTO customerDTO) {
        if (customerRepo.existsByPhone(customerDTO.getPhone())) {
            throw new ConflictException("Customer with phone number already exists!");
        }
        if (customerRepo.existsByEmail(customerDTO.getEmail())) {
            throw new ConflictException("Customer with email id already exists!");
        }
        Customer customer = customerMapper.toEntity(customerDTO);
        Customer savedCustomer = customerRepo.save(customer);
        
        return customerMapper.toDTO(savedCustomer);
    }
    
    public CustomerDTO updateCustomer(String id, CustomerDTO customerDTO) {
        
        Customer existingCustomer = customerRepo.findById(UUID.fromString(id))
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        
        if (customerDTO.getName() != null) existingCustomer.setName(customerDTO.getName());
        if (customerDTO.getAddress() != null) existingCustomer.setAddress(customerDTO.getAddress());
        if (customerDTO.getEmail() != null) existingCustomer.setEmail(customerDTO.getEmail());
        if (customerDTO.getPhone() != null) existingCustomer.setPhone(customerDTO.getPhone());
        if (customerDTO.getAge() != null) existingCustomer.setAge(customerDTO.getAge());
        Customer updatedCustomer = customerRepo.save(existingCustomer);
        
        return customerMapper.toDTO(updatedCustomer);
    }
    
    public void deleteCustomer(String id) {
        
        if (!customerRepo.existsById(UUID.fromString(id))) {
            throw new ResourceNotFoundException("Customer not found with id: " + id);
        }
        customerRepo.deleteById(UUID.fromString(id));
    }
    
}
