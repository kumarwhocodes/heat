package com.zerobee.heat.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "feedbacks")
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    private UUID fileId;

    @Column(name = "overall_rating", nullable = false)
    private Float overallRating;

    @Column(name = "planning_rating", nullable = false)
    private Integer planningRating;

    @Column(name = "booking_rating", nullable = false)
    private Integer bookingRating;

    @Column(name = "service_rating", nullable = false)
    private Integer serviceRating;

    @Column(name = "recommendation_score", nullable = false)
    private Integer recommendationScore;

    @Column(name = "liked_most")
    private String likedMost;

    @Column(name = "suggestions")
    private String suggestions;

    @Column(name = "is_anonymous", nullable = false)
    private Boolean isAnonymous;

    @Column(name = "submitted_from")
    private String submittedFrom;

    @Column(name = "submitted_at", nullable = false)
    private Instant submittedAt;
}
