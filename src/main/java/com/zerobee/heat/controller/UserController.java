package com.zerobee.heat.controller;

import com.zerobee.heat.dto.CustomResponse;
import com.zerobee.heat.dto.UserDTO;
import com.zerobee.heat.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    public CustomResponse<UserDTO> createUser(@Valid @RequestBody UserDTO userDTO) {
        return new CustomResponse<>(HttpStatus.CREATED, "User created successfully", userService.createUser(userDTO));
    }
    
    @GetMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public CustomResponse<List<UserDTO>> getAllUsers() {
        return new CustomResponse<>(HttpStatus.OK, "All users fetched", userService.getAllUsers());
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public CustomResponse<UserDTO> getUserById(@PathVariable String id) {
        return new CustomResponse<>(HttpStatus.OK, "User fetched by ID", userService.getUserById(id));
    }
    
    @GetMapping("/email/{email}")
    @PreAuthorize("hasRole('ADMIN') or #email == authentication.principal.username")
    public CustomResponse<UserDTO> getUserByEmail(@PathVariable String email) {
        return new CustomResponse<>(HttpStatus.OK, "User fetched by email", userService.getUserByEmail(email));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public CustomResponse<UserDTO> updateUser(@PathVariable String id, @Valid @RequestBody UserDTO userDTO) {
        return new CustomResponse<>(HttpStatus.OK, "User updated successfully", userService.updateUser(id, userDTO));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public CustomResponse<Void> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return new CustomResponse<>(HttpStatus.OK, "User deleted successfully", null);
    }
    
    /**
     * Role management endpoints - only accessible to admins
     */
    
    @PostMapping("/{userId}/roles/{roleName}")
    @PreAuthorize("hasRole('ADMIN')")
    public CustomResponse<UserDTO> addRoleToUser(@PathVariable String userId, @PathVariable String roleName) {
        return new CustomResponse<>(HttpStatus.OK, "Role added to user", userService.addRoleToUser(userId, roleName));
    }
    
    @DeleteMapping("/{userId}/roles/{roleName}")
    @PreAuthorize("hasRole('ADMIN')")
    public CustomResponse<UserDTO> removeRoleFromUser(@PathVariable String userId, @PathVariable String roleName) {
        return new CustomResponse<>(HttpStatus.OK, "Role removed from user", userService.removeRoleFromUser(userId, roleName));
    }
}
