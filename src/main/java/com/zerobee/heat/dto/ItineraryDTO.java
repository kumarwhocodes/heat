package com.zerobee.heat.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItineraryDTO {
    private UUID itineraryId;

    private String agentName;
    private String agentEmail;
    private String agentPhone;

    private String numAdult;
    private String numChildren;

    private String tourCode;
    private String destination;

    private String duration;
    private LocalDate startDate;
    private LocalDate endDate;
    private String noOfDays;
    private String noOfNights;
    private String arrival;

    private List<DayWiseDTO> dayWiseList;
}
