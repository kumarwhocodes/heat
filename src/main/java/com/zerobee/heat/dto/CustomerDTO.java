package com.zerobee.heat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDTO {

    private String customerId;
    
    @NotBlank(message = "Name is required")
    @NotNull
    private String clientName;
    @NotNull
    @NotBlank(message = "Email is required")
    private String clientEmail;
    @NotNull
    @NotBlank(message = "Phone Number is required")
    @Pattern(regexp = "^[0-9]\\d{9}$", message = "Invalid phone number")
    private String clientPhone;
    @NotBlank(message = "Nationality is required")
    @NotNull
    private String nationality;
    @NotBlank(message = "Alternate Phone is required")
    @NotNull
    private String clientAlternatePhone;
    private String clientLanguage;

    private List<ItineraryDTO> itineraries;
}