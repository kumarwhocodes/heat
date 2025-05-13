package com.zerobee.heat.repo;

import com.zerobee.heat.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepo extends JpaRepository<Role, UUID> {
    Optional<Role> findByNameIgnoreCase(String name);
    
    boolean existsByName(String name);
    
}
