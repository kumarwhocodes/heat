package com.zerobee.heat.dto;

import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DayWiseDTO {
    private UUID dayWiseId;
    private String date;
    private String day;
    private String destination;
    private String notes;
}
