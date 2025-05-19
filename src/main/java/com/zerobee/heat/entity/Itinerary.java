package com.zerobee.heat.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "itinerary")
public class Itinerary {

    @Id
    private int itineraryId;

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

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToMany(mappedBy = "itinerary", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DayWise> dayWiseList = new ArrayList<DayWise>();
}
