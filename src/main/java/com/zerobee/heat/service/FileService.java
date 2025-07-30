package com.zerobee.heat.service;

import com.zerobee.heat.dto.CreateFileRequestDTO;
import com.zerobee.heat.dto.FileDTO;
import com.zerobee.heat.entity.Customer;
import com.zerobee.heat.entity.File;
import com.zerobee.heat.entity.Itinerary;
import com.zerobee.heat.enums.FileStage;
import com.zerobee.heat.enums.FileStatus;
import com.zerobee.heat.enums.PencilBookingStatus;
import com.zerobee.heat.exception.ConflictException;
import com.zerobee.heat.exception.ResourceNotFoundException;
import com.zerobee.heat.mapper.CustomerMapper;
import com.zerobee.heat.mapper.FileMapper;
import com.zerobee.heat.mapper.ItineraryMapper;
import com.zerobee.heat.repo.CustomerRepo;
import com.zerobee.heat.repo.FileRepo;
import com.zerobee.heat.repo.ItineraryRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FileService {
    
    private final FileRepo fileRepo;
    private final FileMapper fileMapper;
    private final CustomerMapper customerMapper;
    private final ItineraryMapper itineraryMapper;
    private final CustomerRepo customerRepo;
    private final ItineraryRepo itineraryRepo;
    
    public List<FileDTO> getAllFiles() {
        List<File> files = fileRepo.findAllWithDetails();
        return files.stream()
                .map(fileMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    public FileDTO getFileById(UUID id) {
        File file = fileRepo.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("File not found with id: " + id));
        return fileMapper.toDTO(file);
    }
    
    public List<FileDTO> getFilesByCustomerId(String customerId) {
        List<File> files = fileRepo.findByCustomerId(customerId);
        return files.stream()
                .map(fileMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    public List<FileDTO> getFilesByItineraryId(UUID itineraryId) {
        List<File> files = fileRepo.findByItineraryId(itineraryId);
        return files.stream()
                .map(fileMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    public List<FileDTO> getFilesByStatus(FileStatus status) {
        List<File> files = fileRepo.findByStatus(status);
        return files.stream()
                .map(fileMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    public List<FileDTO> getFilesByFheId(String fheId) {
        List<File> files = fileRepo.findByFheId(fheId);
        if (files.isEmpty()) {
            throw new ResourceNotFoundException("No files found with FHE ID: " + fheId);
        }
        return files.stream()
                .map(fileMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public FileDTO createFile(CreateFileRequestDTO request) {
        // Validate and fetch the related entities using the IDs
        Customer customer = customerRepo.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + request.getCustomerId()));
        
        Itinerary itinerary = itineraryRepo.findById(request.getItineraryId())
                .orElseThrow(() -> new ResourceNotFoundException("Itinerary not found with id: " + request.getItineraryId()));
        
        // Create File entity with the IDs (internal use)
        File file = File.builder()
                .vehiclePencilBooking(request.getVehiclePencilBooking())
                .hotelPencilBooking(request.getHotelPencilBooking())
                .status(FileStatus.PENDING)
                .isMature(false)
                .fheId(null)
                .customerId(request.getCustomerId())    // Store ID internally
                .itineraryId(request.getItineraryId())  // Store ID internally
                .stage(FileStage.SALES)
                .build();
        
        File savedFile = fileRepo.save(file);
        
        // Build response with full objects (no IDs exposed)
        return FileDTO.builder()
                .id(savedFile.getId())
                .vehiclePencilBooking(savedFile.getVehiclePencilBooking())
                .hotelPencilBooking(savedFile.getHotelPencilBooking())
                .status(savedFile.getStatus())
                .isMature(savedFile.getIsMature())
                .fheId(savedFile.getFheId())
                .customer(customerMapper.toDTO(customer))        // Full CustomerDTO
                .itinerary(itineraryMapper.toDTO(itinerary))     // Full ItineraryDTO
                .stage(savedFile.getStage())
                .build();
    }
    
    
    @Transactional
    public FileDTO updateFile(UUID id, FileDTO fileDTO) {
        File existingFile = fileRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("File not found with id: " + id));
        
        // Check FHE ID conflict
        if (fileDTO.getFheId() != null) {
            throw new ConflictException("File with FHE ID already exists: " + fileDTO.getFheId());
        }
        
        // Update fields
        if (fileDTO.getVehiclePencilBooking() != null)
            existingFile.setVehiclePencilBooking(fileDTO.getVehiclePencilBooking());
        if (fileDTO.getHotelPencilBooking() != null)
            existingFile.setHotelPencilBooking(fileDTO.getHotelPencilBooking());
        if (fileDTO.getStatus() != null) existingFile.setStatus(fileDTO.getStatus());
        if (fileDTO.getIsMature() != null) existingFile.setIsMature(fileDTO.getIsMature());
        if (fileDTO.getFheId() != null) existingFile.setFheId(fileDTO.getFheId());
        if (fileDTO.getStage() != null) existingFile.setStage(fileDTO.getStage());
        
        File updatedFile = fileRepo.save(existingFile);
        return fileMapper.toDTO(updatedFile);
    }
    
    @Transactional
    public void deleteFile(UUID id) {
        if (!fileRepo.existsById(id)) {
            throw new ResourceNotFoundException("File not found with id: " + id);
        }
        fileRepo.deleteById(id);
    }
    
    // Additional business methods
    public List<FileDTO> getMatureFiles() {
        List<File> files = fileRepo.findByIsMature(true);
        return files.stream()
                .map(fileMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    public List<FileDTO> getImmatureFiles() {
        List<File> files = fileRepo.findByIsMature(false);
        return files.stream()
                .map(fileMapper::toDTO)
                .collect(Collectors.toList());
    }
}
