package com.zerobee.heat.dto;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackDTO {

    private UUID id;

    private String customerId;
    private UUID fileId;

    private Float overallRating;
    private Integer planningRating;
    private Integer bookingRating;
    private Integer serviceRating;
    private Integer recommendationScore;
    private String likedMost;
    private String suggestions;
    private Boolean isAnonymous;
    private String submittedFrom;
    private Instant submittedAt;
}
