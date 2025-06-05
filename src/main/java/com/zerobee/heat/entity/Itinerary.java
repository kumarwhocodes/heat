package com.zerobee.heat.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
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
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;
    
    @OneToMany(mappedBy = "itinerary", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DayWise> dayWiseList = new ArrayList<>();
    
    // Helper methods for managing bidirectional relationship
    public void addDayWise(DayWise dayWise) {
        dayWiseList.add(dayWise);
        dayWise.setItinerary(this);
    }
    
    public void removeDayWise(DayWise dayWise) {
        dayWiseList.remove(dayWise);
        dayWise.setItinerary(null);
    }
    
}