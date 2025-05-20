package com.zerobee.heat.repo;

import com.zerobee.heat.entity.Itinerary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ItineraryRepo extends JpaRepository<Itinerary, UUID> {
}
