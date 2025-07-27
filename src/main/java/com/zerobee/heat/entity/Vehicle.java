package com.zerobee.heat.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="vehicles")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String vehicle_no;
    private String vehicle_reg_no;
    private String name;
    private String category;
    private String star;
    private LocalDate pollution_end_date;
    private LocalDate insurance_end_date;
    private LocalDate start_date;
    private LocalDate end_date;
    private Boolean challan_check;
    private Boolean availability;

}
