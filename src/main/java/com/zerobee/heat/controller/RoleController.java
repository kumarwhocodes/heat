package com.zerobee.heat.controller;

import com.zerobee.heat.dto.CustomResponse;
import com.zerobee.heat.dto.RoleDTO;
import com.zerobee.heat.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class RoleController {
    
    private final RoleService roleService;
    
    @GetMapping
    public CustomResponse<List<RoleDTO>> getAllRoles() {
        return new CustomResponse<>(HttpStatus.OK, "All roles fetched", roleService.getAllRoles());
    }
    
    @PostMapping
    public CustomResponse<RoleDTO> createRole(@Valid @RequestBody RoleDTO roleDTO) {
        return new CustomResponse<>(HttpStatus.CREATED, "Role created successfully", roleService.createRole(roleDTO));
    }
    
    @PutMapping("/{id}")
    public CustomResponse<RoleDTO> updateRole(@PathVariable String id, @Valid @RequestBody RoleDTO roleDTO) {
        return new CustomResponse<>(HttpStatus.OK, "Role updated successfully", roleService.updateRole(id, roleDTO));
    }
    
    @DeleteMapping("/{id}")
    public CustomResponse<Void> deleteRole(@PathVariable String id) {
        roleService.deleteRole(id);
        return new CustomResponse<>(HttpStatus.OK, "Role deleted successfully", null);
    }
}
