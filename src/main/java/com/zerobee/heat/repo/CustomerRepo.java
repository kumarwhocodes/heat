package com.zerobee.heat.repo;

import com.zerobee.heat.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepo extends JpaRepository<Customer, String> {
    
    boolean existsByPhone(String phone);
    
    boolean existsByEmail(String email);
    
    Optional<Customer> findTopByIdStartingWithOrderByIdDesc(String prefix);
}
