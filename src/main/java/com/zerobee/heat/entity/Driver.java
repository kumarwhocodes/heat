package com.zerobee.heat.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "driver")
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    private String email;
    private String phone;
    private String licenseNumber;
    private LocalDate licenseExpiryDate;
    private String address;
    private String experienceYears;
    private Boolean isAvailable;
    private String grades;

    @OneToOne()
    @JoinColumn(name = "id")
    private Vehicle vehicle;

}
