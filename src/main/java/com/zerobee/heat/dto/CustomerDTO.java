package com.zerobee.heat.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDTO {

    private String customerId;
    
    @NotNull
    private String clientName;
    @NotNull
    private String clientEmail;
    @NotNull
    private String clientPhone;
    @NotNull
    private String nationality;
    @NotNull
    private String clientAlternatePhone;
    private String clientLanguage;

    private List<ItineraryDTO> itineraries;
}