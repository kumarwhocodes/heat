package com.zerobee.heat.service;

import com.zerobee.heat.dto.LeaveDTO;
import com.zerobee.heat.entity.Leave;
import com.zerobee.heat.entity.User;
import com.zerobee.heat.enums.LeaveStatus;
import com.zerobee.heat.exception.ResourceNotFoundException;
import com.zerobee.heat.mapper.LeaveMapper;
import com.zerobee.heat.repo.LeaveRepo;
import com.zerobee.heat.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeaveService {
    
    private final LeaveRepo leaveRepo;
    private final UserRepo userRepo;
    private final LeaveMapper leaveMapper;
    
    @Transactional
    public LeaveDTO applyLeave(String userId, LeaveDTO leaveDTO) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        Leave leave = Leave.builder()
                .user(user)
                .startDate(leaveDTO.getStartDate())
                .endDate(leaveDTO.getEndDate())
                .reason(leaveDTO.getReason())
                .status(LeaveStatus.PENDING)
                .build();
        
        Leave savedLeave = leaveRepo.save(leave);
        return leaveMapper.toDTO(savedLeave);
    }
    
    public List<LeaveDTO> getLeavesByUser(String userId) {
        return leaveRepo.findByUserId(userId).stream()
                .map(leaveMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    public List<LeaveDTO> getAllLeaves() {
        return leaveRepo.findAll().stream()
                .map(leaveMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public LeaveDTO approveLeave(UUID leaveId, boolean approved) {
        Leave leave = leaveRepo.findById(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found with id: " + leaveId));
        
        leave.setStatus(approved ? LeaveStatus.APPROVED : LeaveStatus.REJECTED);
        Leave updatedLeave = leaveRepo.save(leave);
        return leaveMapper.toDTO(updatedLeave);
    }
}
