package com.zerobee.heat.repo;

import com.zerobee.heat.entity.Leave;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LeaveRepo extends JpaRepository<Leave, UUID> {
    List<Leave> findByUserId(String userId);
}
