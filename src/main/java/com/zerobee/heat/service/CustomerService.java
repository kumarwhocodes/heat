package com.zerobee.heat.service;

import com.zerobee.heat.dto.CustomerDTO;
import com.zerobee.heat.entity.Customer;
import com.zerobee.heat.exception.ResourceNotFoundException;
import com.zerobee.heat.mapper.CustomerMapper;
import com.zerobee.heat.repo.CustomerRepo;
import jakarta.transaction.Transactional;
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

    public CustomerDTO getCustomerById(UUID id) {
        Customer customer = customerRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        return customerMapper.toDTO(customer);
    }

    public CustomerDTO createCustomer(CustomerDTO customerDTO) {

        Customer customer = customerMapper.toEntity(customerDTO);
        Customer savedCustomer = customerRepo.save(customer);

        return customerMapper.toDTO(savedCustomer);
    }

    public CustomerDTO updateCustomer(UUID id,CustomerDTO customerDTO) {

        Customer existingCustomer = customerRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));

        existingCustomer.setName(customerDTO.getName());
        existingCustomer.setAddress(customerDTO.getAddress());
        existingCustomer.setEmail(customerDTO.getEmail());
        existingCustomer.setPhone(customerDTO.getPhone());
        existingCustomer.setAge(customerDTO.getAge());
        Customer updatedCustomer = customerRepo.save(existingCustomer);

        return customerMapper.toDTO(updatedCustomer);
    }

    public void deleteCustomer(UUID id) {

        if(!customerRepo.existsById(id)){
            throw new ResourceNotFoundException("Customer not found with id: " + id);
        }
        customerRepo.deleteById(id);
    }

}
