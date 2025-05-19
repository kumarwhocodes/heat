package com.zerobee.heat.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DayWiseDto {
    private Integer dayWiseId;
    private String day;
}
