package com.zerobee.heat.dto;

import com.zerobee.heat.enums.FileStatus;
import com.zerobee.heat.enums.PencilBookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileDTO {
    private UUID id;
    private PencilBookingStatus pencilBooking;
    private FileStatus status;
    private Boolean isMature;
    private String fheId;
    
    private CustomerDTO customer;
    private ItineraryDTO itinerary;
    
    private Boolean finalItineraryConfirmed;
}


