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
        createRoleIfNotFound("SALES_RESERVATION");
        createRoleIfNotFound("OPERATIONS");
        createRoleIfNotFound("FHE");
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
            adminUser.setId("ADMIN001");
            adminUser.setEmail("admin@heat.com");
            adminUser.setName("DRISHTI DI KE PAPA");
            adminUser.setPassword(passwordEncoder.encode("admin123"));
            
           
            
            // Assign ADMIN role
            Set<Role> roles = new HashSet<>();
            roleRepo.findByNameIgnoreCase("ADMIN").ifPresent(roles::add);
            roleRepo.findByNameIgnoreCase("USER").ifPresent(roles::add);
            adminUser.setRoles(roles);
            
            userRepo.save(adminUser);
        }
        
        if (!userRepo.existsByEmail("sales@heat.com")) {
            // Create admin user
            User salesUser = new User();
            salesUser.setId("SALES001");
            salesUser.setEmail("sales@heat.com");
            salesUser.setName("SALES KA BAAP");
            salesUser.setPassword(passwordEncoder.encode("sales123"));
            
            // Assign ADMIN role
            Set<Role> roles = new HashSet<>();
            roleRepo.findByNameIgnoreCase("SALES").ifPresent(roles::add);
            roleRepo.findByNameIgnoreCase("USER").ifPresent(roles::add);
            salesUser.setRoles(roles);
            
            userRepo.save(salesUser);
        }

        if (!userRepo.existsByEmail("sales_reservation@heat.com")) {
            // Create admin user
            User salesUser = new User();
            salesUser.setId("SALES_RESERVATION001");
            salesUser.setEmail("sales_reservation@heat.com");
            salesUser.setName("SALES RESERVATION KA BAAP");
            salesUser.setPassword(passwordEncoder.encode("sales_reservation123"));

            // Assign ADMIN role
            Set<Role> roles = new HashSet<>();
            roleRepo.findByNameIgnoreCase("SALES_RESERVATION").ifPresent(roles::add);
            roleRepo.findByNameIgnoreCase("USER").ifPresent(roles::add);
            salesUser.setRoles(roles);

            userRepo.save(salesUser);
        }

        if (!userRepo.existsByEmail("operations@heat.com")) {
            User operationsUser = new User();
            operationsUser.setId("OPERATIONS001");
            operationsUser.setEmail("operations@heat.com");
            operationsUser.setName("OPERATIONS KA BAAP");
            operationsUser.setPassword(passwordEncoder.encode("operations123"));

            Set<Role> roles = new HashSet<>();
            roleRepo.findByNameIgnoreCase("OPERATIONS").ifPresent(roles::add);
            roleRepo.findByNameIgnoreCase("USER").ifPresent(roles::add);
            operationsUser.setRoles(roles);

            userRepo.save(operationsUser);
        }

        if (!userRepo.existsByEmail("fhe@heat.com")) {
            User fheUser = new User();
            fheUser.setId("FHE001");
            fheUser.setEmail("fhe@heat.com");
            fheUser.setName("FHE KA BAAP");
            fheUser.setPassword(passwordEncoder.encode("fhe123"));

            Set<Role> roles = new HashSet<>();
            roleRepo.findByNameIgnoreCase("FHE").ifPresent(roles::add);
            roleRepo.findByNameIgnoreCase("USER").ifPresent(roles::add);
            fheUser.setRoles(roles);

            userRepo.save(fheUser);
        }

        if (!userRepo.existsByEmail("accounts@heat.com")) {
            User accountsUser = new User();
            accountsUser.setId("ACCOUNTS001");
            accountsUser.setEmail("accounts@heat.com");
            accountsUser.setName("ACCOUNTS KA BAAP");
            accountsUser.setPassword(passwordEncoder.encode("accounts123"));

            Set<Role> roles = new HashSet<>();
            roleRepo.findByNameIgnoreCase("ACCOUNTS").ifPresent(roles::add);
            roleRepo.findByNameIgnoreCase("USER").ifPresent(roles::add);
            accountsUser.setRoles(roles);

            userRepo.save(accountsUser);
        }
    }
    
    
}