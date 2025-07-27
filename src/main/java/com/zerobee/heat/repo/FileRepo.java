package com.zerobee.heat.repo;

import com.zerobee.heat.entity.File;
import com.zerobee.heat.enums.FileStatus;
import com.zerobee.heat.enums.PencilBookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FileRepo extends JpaRepository<File, Integer> {
    
    // Find files by status
    List<File> findByStatus(FileStatus status);
    
    // Find files by pencil booking status
    List<File> findByPencilBooking(PencilBookingStatus pencilBooking);
    
    // Find files by customer ID
    List<File> findByCustomerId(String customerId);
    
    // Find files by itinerary ID
    List<File> findByItineraryId(UUID itineraryId);
    
    // Find files by FHE ID
    List<File> findByFheId(String fheId);
    
    // Check if file exists by FHE ID
    boolean existsByFheId(String fheId);
    
    // Find files by maturity status
    List<File> findByIsMature(Boolean isMature);
    
    // Custom query to find files with complete details
    @Query("SELECT f FROM File f LEFT JOIN FETCH f.customer LEFT JOIN FETCH f.itinerary WHERE f.id = :id")
    Optional<File> findByIdWithDetails(@Param("id") Integer id);
    
    // Find all files with their related entities
    @Query("SELECT f FROM File f LEFT JOIN FETCH f.customer LEFT JOIN FETCH f.itinerary")
    List<File> findAllWithDetails();
}
