package com.zerobee.heat.service;

import com.zerobee.heat.dto.RoleDTO;
import com.zerobee.heat.entity.Role;
import com.zerobee.heat.exception.ConflictException;
import com.zerobee.heat.exception.ResourceNotFoundException;
import com.zerobee.heat.mapper.RoleMapper;
import com.zerobee.heat.repo.RoleRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {
    
    private final RoleRepo roleRepo;
    private final RoleMapper roleMapper;
    
    public List<RoleDTO> getAllRoles() {
        List<Role> roles = roleRepo.findAll();
        return roles.stream()
                .map(roleMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public RoleDTO createRole(RoleDTO roleDTO) {
        if (roleRepo.existsByName(roleDTO.getName())) {
            throw new IllegalArgumentException("Role name already exists");
        }
        
        Role role = roleMapper.toEntity(roleDTO);
        Role savedRole = roleRepo.save(role);
        
        return roleMapper.toDTO(savedRole);
    }
    
    @Transactional
    public RoleDTO updateRole(String id, RoleDTO roleDTO) {
        Role existingRole = roleRepo.findById(UUID.fromString(id))
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));
        
        // Check if new name doesn't conflict with other roles
        if (!existingRole.getName().equals(roleDTO.getName()) &&
                roleRepo.existsByName(roleDTO.getName())) {
            throw new ConflictException("Role already exists");
        }
        
        existingRole.setName(roleDTO.getName());
        Role updatedRole = roleRepo.save(existingRole);
        
        return roleMapper.toDTO(updatedRole);
    }
    
    @Transactional
    public void deleteRole(String id) {
        if (!roleRepo.existsById(UUID.fromString(id))) {
            throw new ResourceNotFoundException("Role not found with id: " + id);
        }
        roleRepo.deleteById(UUID.fromString(id));
    }
}
