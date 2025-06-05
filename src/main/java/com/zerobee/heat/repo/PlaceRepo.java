package com.zerobee.heat.repo;

import com.zerobee.heat.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PlaceRepo extends JpaRepository<Place, UUID> {
    Optional<Place> findByNameIgnoreCase(String placeName);
}
