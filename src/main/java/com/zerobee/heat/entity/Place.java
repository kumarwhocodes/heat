package com.zerobee.heat.entity;

import com.zerobee.heat.enums.PlaceType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "places")
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;
    private String code;

    @Enumerated(EnumType.STRING)
    private PlaceType type;

    private String location;
    private String elevation;
    private String description;

    private String closedOn;
    private boolean requiresPermit;
    @ElementCollection
    private List<String> permitDetails;

    private boolean isActive = true;

    private String imageUrl;
    private String bestTimeToVisit;

    private Double lat;
    private Double lng;

    private String countryCode; // "IN", "NP", etc.
}
