package com.zerobee.heat.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItineraryDto {
    private Integer itineraryId;

    private String agentName;
    private String agentEmail;
    private String agentPhone;

    private Integer numAdult;
    private Integer numChildren;

    private String tourCode;
    private String destination;

    private String duration;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer noOfDays;
    private Integer noOfNights;
    private String arrival;

    private List<DayWiseDto> dayWiseList;
}
