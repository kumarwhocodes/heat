package com.zerobee.heat.dto;

import com.zerobee.heat.enums.PlaceType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaceDTO {
    
    private String id;
    
    private String name;
    private String code;
    
    @Enumerated(EnumType.STRING)
    private PlaceType type;
    
    private String location;
    private String elevation;
    private String description;
    
    private String closedOn;
    private boolean requiresPermit;
    private List<String> permitDetails;
    
    private boolean isActive = true;
    
    private String imageUrl;
    private String bestTimeToVisit;
    
    private Double lat;
    private Double lng;
    
    private String countryCode; // "IN", "NP", etc.
}