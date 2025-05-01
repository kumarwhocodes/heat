package com.zerobee.heat.controller;

import com.zerobee.heat.dto.RoleDTO;
import com.zerobee.heat.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<RoleDTO>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<RoleDTO> getRoleById(@PathVariable Integer id) {
        return ResponseEntity.ok(roleService.getRoleById(id));
    }
    
    @GetMapping("/name/{name}")
    public ResponseEntity<RoleDTO> getRoleByName(@PathVariable String name) {
        return ResponseEntity.ok(roleService.getRoleByName(name));
    }
    
    @PostMapping
    public ResponseEntity<RoleDTO> createRole(@Valid @RequestBody RoleDTO roleDTO) {
        return new ResponseEntity<>(roleService.createRole(roleDTO), HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<RoleDTO> updateRole(@PathVariable Integer id, @Valid @RequestBody RoleDTO roleDTO) {
        return ResponseEntity.ok(roleService.updateRole(id, roleDTO));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable Integer id) {
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }
}