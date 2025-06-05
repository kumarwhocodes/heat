package com.zerobee.heat.controller;

import com.zerobee.heat.dto.CustomResponse;
import com.zerobee.heat.dto.PlaceDTO;
import com.zerobee.heat.service.PlaceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/places")
public class PlaceController {
    
    private final PlaceService placeService;
    
    @GetMapping
    public CustomResponse<List<PlaceDTO>> getAllPlaces() {
        return new CustomResponse<>(HttpStatus.OK, "All places fetched", placeService.getAllPlaces());
    }
    
    @GetMapping("/nearby")
    public CustomResponse<List<PlaceDTO>> getNearbyPlaces(
            @RequestParam("place") String place,
            @RequestParam(defaultValue = "10") Double radiusInKm) {
        return new CustomResponse<>(HttpStatus.OK, "Nearby places fetched",
                placeService.findNearbyPlaces(place, radiusInKm));
    }
    
    @GetMapping("/name/{placeName}")
    public CustomResponse<PlaceDTO> getPlaceByName(@PathVariable String placeName){
        return new CustomResponse<>(HttpStatus.OK, "Place Fetched", placeService.getPlaceByName(placeName));
    }
    
    @GetMapping("/{id}")
    public CustomResponse<PlaceDTO> getPlaceById(@PathVariable String id) {
        return new CustomResponse<>(HttpStatus.OK, "Place fetched", placeService.getPlaceById(id));
    }
    
    @PostMapping
    public CustomResponse<PlaceDTO> createPlace(@Valid @RequestBody PlaceDTO placeDTO) {
        return new CustomResponse<>(HttpStatus.CREATED, "Place created", placeService.createPlace(placeDTO));
    }
    
    @PutMapping("/{id}")
    public CustomResponse<PlaceDTO> updatePlace(@PathVariable String id, @RequestBody PlaceDTO placeDTO) {
        return new CustomResponse<>(HttpStatus.OK, "Place updated", placeService.updatePlace(id, placeDTO));
    }
    
    @DeleteMapping("/{id}")
    public CustomResponse<Void> deletePlace(@PathVariable String id) {
        placeService.deletePlace(id);
        return new CustomResponse<>(HttpStatus.NO_CONTENT, "Place deleted", null);
    }
}
