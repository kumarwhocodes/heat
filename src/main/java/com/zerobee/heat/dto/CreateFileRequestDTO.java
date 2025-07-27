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
public class CreateFileRequestDTO {
    private PencilBookingStatus pencilBooking;
    private FileStatus status;
    private Boolean isMature;
    private String fheId;
    
    private String customerId;
    private UUID itineraryId;
}
