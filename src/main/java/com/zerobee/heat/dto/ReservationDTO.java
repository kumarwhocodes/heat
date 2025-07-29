package com.zerobee.heat.dto;

import com.zerobee.heat.enums.ReservationStatus;
import com.zerobee.heat.enums.ReservationType;
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
public class ReservationDTO {

    private UUID id;
    private ReservationType type;       // HOTEL, TRANSPORT, ACTIVITY
    private String providerName;
    private String providerContact;
    private String referenceNumber;

    private LocalDate checkInDate;
    private LocalDate checkOutDate;

    private Integer numberOfGuests;
    private Double price;

    private ReservationStatus status;   // PENCIL_BOOKED, CONFIRMED, CANCELLED

    private FileDTO file;
}
