package com.zerobee.heat.repo;

import com.zerobee.heat.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ReservationRepo extends JpaRepository<Reservation, UUID> {
}
