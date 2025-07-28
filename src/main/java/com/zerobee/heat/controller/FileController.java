package com.zerobee.heat.controller;

import com.zerobee.heat.dto.CreateFileRequestDTO;
import com.zerobee.heat.dto.CustomResponse;
import com.zerobee.heat.dto.FileDTO;
import com.zerobee.heat.enums.FileStatus;
import com.zerobee.heat.enums.PencilBookingStatus;
import com.zerobee.heat.service.FileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {
    
    private final FileService fileService;
    
    @GetMapping
    public CustomResponse<List<FileDTO>> getAllFiles() {
        return new CustomResponse<>(HttpStatus.OK, "All files fetched", fileService.getAllFiles());
    }
    
    @GetMapping("/{id}")
    public CustomResponse<FileDTO> getFileById(@PathVariable UUID id) {
        return new CustomResponse<>(HttpStatus.OK, "File fetched", fileService.getFileById(id));
    }
    
    @GetMapping("/fhe/{fheId}")
    public CustomResponse<List<FileDTO>> getFilesByFheId(@PathVariable String fheId) {
        return new CustomResponse<>(HttpStatus.OK, "Files fetched for FHE ID",
                fileService.getFilesByFheId(fheId));
    }
    
    @GetMapping("/customer/{customerId}")
    public CustomResponse<List<FileDTO>> getFilesByCustomerId(@PathVariable String customerId) {
        return new CustomResponse<>(HttpStatus.OK, "Files fetched for customer",
                fileService.getFilesByCustomerId(customerId));
    }
    
    @GetMapping("/itinerary/{itineraryId}")
    public CustomResponse<List<FileDTO>> getFilesByItineraryId(@PathVariable UUID itineraryId) {
        return new CustomResponse<>(HttpStatus.OK, "Files fetched for itinerary",
                fileService.getFilesByItineraryId(itineraryId));
    }
    
    @GetMapping("/status/{status}")
    public CustomResponse<List<FileDTO>> getFilesByStatus(@PathVariable FileStatus status) {
        return new CustomResponse<>(HttpStatus.OK, "Files fetched by status",
                fileService.getFilesByStatus(status));
    }
    
    @GetMapping("/pencil-booking/{pencilBooking}")
    public CustomResponse<List<FileDTO>> getFilesByPencilBooking(@PathVariable PencilBookingStatus pencilBooking) {
        return new CustomResponse<>(HttpStatus.OK, "Files fetched by pencil booking status",
                fileService.getFilesByPencilBooking(pencilBooking));
    }
    
    @GetMapping("/mature")
    public CustomResponse<List<FileDTO>> getMatureFiles() {
        return new CustomResponse<>(HttpStatus.OK, "Mature files fetched", fileService.getMatureFiles());
    }
    
    @GetMapping("/immature")
    public CustomResponse<List<FileDTO>> getImmatureFiles() {
        return new CustomResponse<>(HttpStatus.OK, "Immature files fetched", fileService.getImmatureFiles());
    }
    
    @PostMapping
    public CustomResponse<FileDTO> createFile(@Valid @RequestBody CreateFileRequestDTO request) {
        return new CustomResponse<>(HttpStatus.CREATED, "File created", fileService.createFile(request));
    }
    
    @PutMapping("/{id}")
    public CustomResponse<FileDTO> updateFile(@PathVariable UUID id, @RequestBody FileDTO fileDTO) {
        return new CustomResponse<>(HttpStatus.OK, "File updated", fileService.updateFile(id, fileDTO));
    }
    
    @DeleteMapping("/{id}")
    public CustomResponse<Void> deleteFile(@PathVariable UUID id) {
        fileService.deleteFile(id);
        return new CustomResponse<>(HttpStatus.NO_CONTENT, "File deleted", null);
    }
}
