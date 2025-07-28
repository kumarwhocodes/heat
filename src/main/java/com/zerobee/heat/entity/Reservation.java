package com.zerobee.heat.entity;

import com.zerobee.heat.enums.ReservationStatus;
import com.zerobee.heat.enums.ReservationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "reservations")
public class Reservation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Enumerated(EnumType.STRING)
    private ReservationType type; // HOTEL, TRANSPORT, ACTIVITY
    
    private String providerName;
    private String providerContact;
    private String referenceNumber;
    
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    
    private Integer numberOfGuests;
    private Double price;
    
    @Enumerated(EnumType.STRING)
    private ReservationStatus status; // PENDING, CONFIRMED, CANCELLED
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id")
    private File file;
}

