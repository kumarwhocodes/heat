package com.zerobee.heat.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDTO {

    private String customerId;

    private String clientName;
    private String clientEmail;
    private String clientPhone;
    private String nationality;
    private String clientEmergencyPhone;
    private String clientLanguage;

    private List<ItineraryDTO> itineraries;
}