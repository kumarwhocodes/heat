package com.zerobee.heat.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "customers")
@ToString(exclude = "itineraries")
public class Customer {
    
    @Id
    private String customerId;
    private String clientName;
    private String clientEmail;
    private String clientPhone;
    private String nationality;
    private String clientAlternatePhone;
    private String clientLanguage;
    
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Itinerary> itineraries = new ArrayList<>();
    
    // Helper methods for managing bidirectional relationship
    public void addItinerary(Itinerary itinerary) {
        itineraries.add(itinerary);
        itinerary.setCustomer(this);
    }
    
    public void removeItinerary(Itinerary itinerary) {
        itineraries.remove(itinerary);
        itinerary.setCustomer(null);
    }
}