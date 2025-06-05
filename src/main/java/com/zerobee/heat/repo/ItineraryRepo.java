package com.zerobee.heat.repo;

import com.zerobee.heat.entity.Itinerary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ItineraryRepo extends JpaRepository<Itinerary, UUID> {
    List<Itinerary> findByCustomer_CustomerId(String customerId);
}
