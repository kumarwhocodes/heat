package com.zerobee.heat.service;

import com.zerobee.heat.dto.AuthenticationRequest;
import com.zerobee.heat.dto.AuthenticationResponse;
import com.zerobee.heat.dto.RoleDTO;
import com.zerobee.heat.dto.UserDTO;
import com.zerobee.heat.entity.Role;
import com.zerobee.heat.entity.User;
import com.zerobee.heat.exception.ConflictException;
import com.zerobee.heat.exception.ResourceNotFoundException;
import com.zerobee.heat.mapper.UserMapper;
import com.zerobee.heat.repo.RoleRepo;
import com.zerobee.heat.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    
    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailServiceSMTP emailServiceSMTP;
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }
    
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepo.findAll();
        return users.stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    public UserDTO getUserById(String id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return userMapper.toDTO(user);
    }
    
    public UserDTO getUserByEmail(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return userMapper.toDTO(user);
    }
    
    /**
     * Create a new user - only accessible to admins
     */
    @Transactional
    public UserDTO createUser(UserDTO userDTO) {
        if (userRepo.existsById(userDTO.getId())) {
            throw new ConflictException("ID already taken!");
        }
        if (userRepo.existsByEmail(userDTO.getEmail())) {
            throw new ConflictException("Email already in use");
        }
        String password = userDTO.getPassword();                //storing password to pass it in the email
        // Encode password
        userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        
        User user = userMapper.toEntity(userDTO);
        
        Set<Role> roles = new HashSet<>();
        if (userDTO.getRoles() != null && !userDTO.getRoles().isEmpty()) {
            for (RoleDTO roleDTO : userDTO.getRoles()) {
                Role role = roleRepo.findByNameIgnoreCase(roleDTO.getName())
                        .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleDTO.getName()));
                roles.add(role);
            }
        } else {
            Role defaultRole = roleRepo.findByNameIgnoreCase("USER")
                    .orElseThrow(() -> new ResourceNotFoundException("Default role not found"));
            roles.add(defaultRole);
        }
        user.setRoles(roles);
        
        User savedUser = userRepo.save(user);
        emailServiceSMTP.sendWelcomeEmail(userMapper.toDTO(savedUser), password);
        return userMapper.toDTO(savedUser);
    }
    
    /**
     * Authenticate user and generate JWT token
     */
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        // Authenticate user credentials
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        
        // Find user and generate token
        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + request.getEmail()));
        
        String jwtToken = jwtService.generateToken(user);
        
        return AuthenticationResponse.builder()
                .name(user.getName())
                .roles(user.getRoles())
                .token(jwtToken)
                .build();
    }
    
    @Transactional
    public UserDTO updateUser(String id, UserDTO userDTO) {
        User existingUser = userRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        
        if (userDTO.getEmail() != null) existingUser.setEmail(userDTO.getEmail());
        if (userDTO.getName() != null) existingUser.setName(userDTO.getName());
        
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }
        
        // Update roles if provided
        if (userDTO.getRoles() != null) {
            Set<Role> roles = new HashSet<>();
            for (RoleDTO roleDTO : userDTO.getRoles()) {
                Role role = roleRepo.findByNameIgnoreCase(roleDTO.getName())
                        .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleDTO.getName()));
                roles.add(role);
            }
            existingUser.setRoles(roles);
        }
        
        User updatedUser = userRepo.save(existingUser);
        return userMapper.toDTO(updatedUser);
    }
    
    @Transactional
    public void deleteUser(String id) {
        if (!userRepo.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepo.deleteById(id);
    }
}