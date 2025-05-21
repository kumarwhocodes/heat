package com.zerobee.heat.controller;

import com.zerobee.heat.dto.CustomResponse;
import com.zerobee.heat.dto.CustomerDTO;
import com.zerobee.heat.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {
    
    private final CustomerService customerService;
    
    @GetMapping
    public CustomResponse<List<CustomerDTO>> getAllCustomers() {
        return new CustomResponse<>(HttpStatus.OK, "All customers fetched", customerService.getAllCustomers());
    }
    
    @GetMapping("/{id}")
    public CustomResponse<CustomerDTO> getCustomer(@PathVariable String id) {
        return new CustomResponse<>(HttpStatus.OK, "Customer fetched", customerService.getCustomerById(id));
    }
    
    @PostMapping
    public CustomResponse<CustomerDTO> createCustomer(@Valid @RequestBody CustomerDTO customerDTO) {
        return new CustomResponse<>(HttpStatus.CREATED, "Customer created", customerService.createCustomer(customerDTO));
    }
    
    @PutMapping("/{id}")
    public CustomResponse<CustomerDTO> updateCustomer(@PathVariable String id, @RequestBody CustomerDTO customerDTO) {
        return new CustomResponse<>(HttpStatus.OK, "Customer updated", customerService.updateCustomer(id, customerDTO));
    }
    
    @DeleteMapping("/{id}")
    public CustomResponse<Void> deleteCustomer(@PathVariable String id) {
        customerService.deleteCustomer(id);
        return new CustomResponse<>(HttpStatus.NO_CONTENT, "Customer deleted", null);
    }
}