package com.zerobee.heat.repo;

import com.zerobee.heat.entity.Customer;
import com.zerobee.heat.entity.DayWise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DayWiseRepo extends JpaRepository<DayWise, UUID> {

    List<DayWise> findByItinerary_ItineraryId(UUID itineraryId);

}
