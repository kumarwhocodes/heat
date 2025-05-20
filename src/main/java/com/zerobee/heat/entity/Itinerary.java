package com.zerobee.heat.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "itinerary")
@ToString(exclude = {"customer", "dayWiseList"})
public class Itinerary {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
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

    @ManyToOne
    @JoinColumn(name = "customer_id")
    @JsonBackReference
    private Customer customer;

    @OneToMany(mappedBy = "itinerary", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<DayWise> dayWiseList = new ArrayList<>();
}
