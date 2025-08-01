package com.zerobee.heat.controller;

import com.zerobee.heat.dto.CustomResponse;
import com.zerobee.heat.dto.LeaveDTO;
import com.zerobee.heat.repo.UserRepo;
import com.zerobee.heat.service.LeaveService;
import com.zerobee.heat.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/leaves")
@RequiredArgsConstructor
public class LeaveController {
    
    private final LeaveService leaveService;
    private final UserRepo userRepo;
    
    @PostMapping("/apply")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public CustomResponse<LeaveDTO> applyLeave(@RequestBody LeaveDTO leaveDTO) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        
        String userId = userRepo.findByEmail(userEmail)
                        .orElseThrow(() -> new RuntimeException("User not found"))
                        .getId();
        
        LeaveDTO appliedLeave = leaveService.applyLeave(userId, leaveDTO);
        return new CustomResponse<>(HttpStatus.CREATED, "Leave applied successfully", appliedLeave);
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public CustomResponse<List<LeaveDTO>> getMyLeaves() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        String userId = userRepo.findByEmail(userEmail)
                        .orElseThrow(() -> new RuntimeException("User not found"))
                        .getId();
        
        List<LeaveDTO> leaves = leaveService.getLeavesByUser(userId);
        return new CustomResponse<>(HttpStatus.OK, "Fetched leaves for current user", leaves);
    }
    
    @GetMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public CustomResponse<List<LeaveDTO>> getAllLeaves() {
        return new CustomResponse<>(HttpStatus.OK, "All leaves fetched", leaveService.getAllLeaves());
    }

    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public CustomResponse<LeaveDTO> approveLeave(@PathVariable UUID id, @RequestParam boolean approved) {
        LeaveDTO updatedLeave = leaveService.approveLeave(id, approved);
        return new CustomResponse<>(HttpStatus.OK, "Leave status updated", updatedLeave);
    }
}
