package com.zerobee.heat.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "dayWise")
public class DayWise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer dayWiseId;

    private String day;

    @ManyToOne
    @JoinColumn(name = "itinerary_id")
    @JsonBackReference
    private Itinerary itinerary;
}
