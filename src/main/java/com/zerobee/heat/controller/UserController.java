package com.zerobee.heat.controller;

import com.zerobee.heat.dto.UserDTO;
import com.zerobee.heat.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    /**
     * User management endpoints - CRUD for Users
     */
    
    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO userDTO) {
        return new ResponseEntity<>(userService.createUser(userDTO), HttpStatus.CREATED);
    }
    
    @GetMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Integer id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }
    
    @GetMapping("/email/{email}")
    @PreAuthorize("hasRole('ADMIN') or #email == authentication.principal.username")
    public ResponseEntity<UserDTO> getUserByEmail(@PathVariable String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Integer id, @Valid @RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.updateUser(id, userDTO));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    
    
    /**
     * Role management endpoints - only accessible to admins
     */
    
    @PostMapping("/{userId}/roles/{roleName}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> addRoleToUser(@PathVariable Integer userId, @PathVariable String roleName) {
        return ResponseEntity.ok(userService.addRoleToUser(userId, roleName));
    }
    
    @DeleteMapping("/{userId}/roles/{roleName}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> removeRoleFromUser(@PathVariable Integer userId, @PathVariable String roleName) {
        return ResponseEntity.ok(userService.removeRoleFromUser(userId, roleName));
    }
}