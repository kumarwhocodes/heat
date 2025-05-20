package com.zerobee.heat.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "dayWise")
@ToString(exclude = "itinerary")
public class DayWise {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID dayWiseId;

    private String date;
    private String day;
    private String destination;
//    private List<Activity> activities;
    private String notes;

    @ManyToOne
    @JoinColumn(name = "itinerary_id")
    @JsonBackReference
    private Itinerary itinerary;
}
