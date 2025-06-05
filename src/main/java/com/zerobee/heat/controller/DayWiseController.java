package com.zerobee.heat.controller;

import com.zerobee.heat.dto.CustomResponse;
import com.zerobee.heat.dto.DayWiseDTO;
import com.zerobee.heat.service.DayWiseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/daywise")
@RequiredArgsConstructor
public class DayWiseController {
    
    private final DayWiseService dayWiseService;
    
    @GetMapping("/itinerary/{itineraryId}")
    public CustomResponse<List<DayWiseDTO>> getDayWiseByItinerary(@PathVariable UUID itineraryId) {
        return new CustomResponse<>(HttpStatus.OK, "DayWise list fetched",
                dayWiseService.getDayWiseByItinerary(itineraryId));
    }
    
    @PostMapping("/itinerary/{itineraryId}")
    public CustomResponse<DayWiseDTO> createDayWise(@PathVariable UUID itineraryId, @RequestBody DayWiseDTO dayWiseDTO) {
        return new CustomResponse<>(HttpStatus.CREATED, "DayWise created",
                dayWiseService.createDayWise(itineraryId, dayWiseDTO));
    }
    
    @PutMapping("/{id}")
    public CustomResponse<DayWiseDTO> updateDayWise(@PathVariable UUID id, @RequestBody DayWiseDTO dayWiseDTO) {
        return new CustomResponse<>(HttpStatus.OK, "DayWise updated",
                dayWiseService.updateDayWise(id, dayWiseDTO));
    }
    
    @DeleteMapping("/{id}")
    public CustomResponse<Void> deleteDayWise(@PathVariable UUID id) {
        dayWiseService.deleteDayWise(id);
        return new CustomResponse<>(HttpStatus.NO_CONTENT, "DayWise deleted", null);
    }
}