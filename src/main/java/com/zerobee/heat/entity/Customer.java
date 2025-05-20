package com.zerobee.heat.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
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
    private String clientEmergencyPhone;
    private String clientLanguage;
    private String description;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Itinerary> itineraries = new ArrayList<>();
}
