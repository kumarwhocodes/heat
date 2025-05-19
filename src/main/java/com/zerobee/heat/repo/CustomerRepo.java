package com.zerobee.heat.repo;

import com.zerobee.heat.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepo extends JpaRepository<Customer, String> {
    
    boolean existsByClientPhone(String phone);

    boolean existsByClientEmail(String email);

    Optional<Customer> findTopByCustomerIdStartingWithOrderByCustomerIdDesc(String prefix);
}
