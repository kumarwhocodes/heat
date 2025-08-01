package com.zerobee.heat.entity;

import com.zerobee.heat.enums.LeaveStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "leaves")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Leave {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    private LocalDate startDate;
    private LocalDate endDate;
    
    @Column(length = 500)
    private String reason;
    
    @Enumerated(EnumType.STRING)
    private LeaveStatus status;  // PENDING, APPROVED, REJECTED
}
