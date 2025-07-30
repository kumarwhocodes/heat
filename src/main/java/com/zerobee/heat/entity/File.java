package com.zerobee.heat.entity;

import com.zerobee.heat.enums.FileStage;
import com.zerobee.heat.enums.FileStatus;
import com.zerobee.heat.enums.PencilBookingStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "files")
public class File {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PencilBookingStatus pencilBooking;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FileStatus status;
    
    private Boolean isMature;
    
    @Column(name = "itinerary_id")
    private UUID itineraryId;
    
    @Column(name = "customer_id")
    private String customerId;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "itinerary_id", insertable = false, updatable = false)
    private Itinerary itinerary;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", insertable = false, updatable = false)
    private Customer customer;
    
    private String fheId;
    
    @OneToMany(mappedBy = "fileId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reservation> reservations = new ArrayList<>();
    
    @OneToMany(mappedBy = "file", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> payments = new ArrayList<>();
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FileStage stage; // SALES, OPERATIONS, CLOSED, DEAD
    
    @Column(nullable = false)
    private Boolean finalItineraryConfirmed;
    
//    public void addReservation(Reservation reservation) {
//        reservations.add(reservation);
//        reservation.setFile(this);
//    }
//
//    public void removeReservation(Reservation reservation) {
//        reservations.remove(reservation);
//        reservation.setFile(null);
//    }
//
//    public void addPayment(Payment payment) {
//        payments.add(payment);
//        payment.setFile(this);
//    }
//
//    public void removePayment(Payment payment) {
//        payments.remove(payment);
//        payment.setFile(null);
//    }
    
}
