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
    @Column(name = "vehicle_reg_no", nullable = false, unique = true)
    private String vehicle_reg_no;
    private String name;
    private String category;
    private String grades;
    private LocalDate pollution_end_date;
    private LocalDate insurance_end_date;
    private LocalDate booking_start_date;
    private LocalDate booking_end_date;
    private Boolean challan_check;
    private Boolean availability;

}
