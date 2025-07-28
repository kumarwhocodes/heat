package com.zerobee.heat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleDTO {

    private String id;
    private String vehicle_no;
    private String vehicle_reg_no;
    private String name;
    private String category;
    private String grades;
    private LocalDate pollution_end_date;
    private LocalDate insurance_end_date;
    private LocalDate start_date;
    private LocalDate end_date;
    private Boolean challan_check;
    private Boolean availability;

}
