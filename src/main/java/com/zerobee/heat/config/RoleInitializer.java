package com.zerobee.heat.config;

import com.zerobee.heat.entity.Role;
import com.zerobee.heat.entity.User;
import com.zerobee.heat.repo.RoleRepo;
import com.zerobee.heat.repo.UserRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class RoleInitializer implements CommandLineRunner {
    
    private final RoleRepo roleRepo;
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    @Transactional
    public void run(String... args) {
        // Create default roles if they don't exist
        createRoleIfNotFound("USER");
        createRoleIfNotFound("ADMIN");
        createRoleIfNotFound("SALES");
        createRoleIfNotFound("SALES_PARTNER");
        createRoleIfNotFound("OPERATIONS");
        createRoleIfNotFound("EXECUTION");
        createRoleIfNotFound("ACCOUNTS");
        
        // Create admin user if not exists
        createAdminUserIfNotFound();
    }
    
    private void createRoleIfNotFound(String name) {
        if (!roleRepo.existsByName(name)) {
            Role role = new Role();
            role.setName(name);
            roleRepo.save(role);
        }
    }
    
    private void createAdminUserIfNotFound() {
        if (!userRepo.existsByEmail("admin@heat.com")) {
            // Create admin user
            User adminUser = new User();
            adminUser.setId("HEAT001");
            adminUser.setEmail("admin@heat.com");
            adminUser.setName("APNA ADMIN");
            adminUser.setPassword(passwordEncoder.encode("admin123"));
            
            // Assign ADMIN role
            Set<Role> roles = new HashSet<>();
            roleRepo.findByNameIgnoreCase("ADMIN").ifPresent(roles::add);
            roleRepo.findByNameIgnoreCase("USER").ifPresent(roles::add);
            adminUser.setRoles(roles);
            
            userRepo.save(adminUser);
        }
    }
}