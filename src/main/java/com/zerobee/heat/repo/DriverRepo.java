package com.zerobee.heat.repo;

import com.zerobee.heat.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DriverRepo extends JpaRepository<Driver, UUID> {
}
