package com.zerobee.heat.service;

import com.zerobee.heat.dto.PlaceDTO;
import com.zerobee.heat.entity.Place;
import com.zerobee.heat.exception.ResourceNotFoundException;
import com.zerobee.heat.mapper.PlaceMapper;
import com.zerobee.heat.repo.PlaceRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.zerobee.heat.utils.NearbyDistanceCalc.calculateDistance;

@Service
@RequiredArgsConstructor
public class PlaceService {
    
    private final PlaceRepo placeRepo;
    private final PlaceMapper placeMapper;
    
    public List<PlaceDTO> getAllPlaces() {
        return placeRepo.findAll().stream()
                .map(placeMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    public List<PlaceDTO> findNearbyPlaces(String placeName, Double radiusInKm) {
        Place place = placeRepo.findByNameIgnoreCase(placeName)
                .orElseThrow(() -> new ResourceNotFoundException("Place not found with name: " + placeName));
        List<Place> allPlaces = placeRepo.findAll();
        
        return allPlaces.stream()
                .filter(p -> p.getLat() != null && p.getLng() != null)
                .filter(p -> {
                    double distance = calculateDistance(place.getLat(), place.getLng(), p.getLat(), p.getLng());
                    return distance <= radiusInKm && distance > 0;
                })
                .map(placeMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    public PlaceDTO getPlaceByName(String placeName) {
        Place place = placeRepo.findByNameIgnoreCase(placeName)
                .orElseThrow(() -> new ResourceNotFoundException("Place not found with name: " + placeName));
        return placeMapper.toDTO(place);
    }
    
    public PlaceDTO getPlaceById(String id) {
        Place place = placeRepo.findById(UUID.fromString(id))
                .orElseThrow(() -> new ResourceNotFoundException("Place not found with id: " + id));
        return placeMapper.toDTO(place);
    }
    
    @Transactional
    public PlaceDTO createPlace(PlaceDTO placeDTO) {
        Place place = placeMapper.toEntity(placeDTO);
        Place savedPlace = placeRepo.save(place);
        return placeMapper.toDTO(savedPlace);
    }
    
    @Transactional
    public PlaceDTO updatePlace(String id, PlaceDTO placeDTO) {
        Place existingPlace = placeRepo.findById(UUID.fromString(id))
                .orElseThrow(() -> new ResourceNotFoundException("Place not found with id: " + id));
        
        if (placeDTO.getName() != null) existingPlace.setName(placeDTO.getName());
        if (placeDTO.getCode() != null) existingPlace.setCode(placeDTO.getCode());
        if (placeDTO.getType() != null) existingPlace.setType(placeDTO.getType());
        if (placeDTO.getLocation() != null) existingPlace.setLocation(placeDTO.getLocation());
        if (placeDTO.getElevation() != null) existingPlace.setElevation(placeDTO.getElevation());
        if (placeDTO.getDescription() != null) existingPlace.setDescription(placeDTO.getDescription());
        if (placeDTO.getClosedOn() != null) existingPlace.setClosedOn(placeDTO.getClosedOn());
        existingPlace.setRequiresPermit(placeDTO.isRequiresPermit());
        if (placeDTO.getPermitDetails() != null) existingPlace.setPermitDetails(placeDTO.getPermitDetails());
        existingPlace.setActive(placeDTO.isActive());
        if (placeDTO.getImageUrl() != null) existingPlace.setImageUrl(placeDTO.getImageUrl());
        if (placeDTO.getBestTimeToVisit() != null) existingPlace.setBestTimeToVisit(placeDTO.getBestTimeToVisit());
        if (placeDTO.getLat() != null) existingPlace.setLat(placeDTO.getLat());
        if (placeDTO.getLng() != null) existingPlace.setLng(placeDTO.getLng());
        if (placeDTO.getCountryCode() != null) existingPlace.setCountryCode(placeDTO.getCountryCode());
        
        Place updatedPlace = placeRepo.save(existingPlace);
        return placeMapper.toDTO(updatedPlace);
    }
    
    @Transactional
    public void deletePlace(String id) {
        if (!placeRepo.existsById(UUID.fromString(id))) {
            throw new ResourceNotFoundException("Place not found with id: " + id);
        }
        placeRepo.deleteById(UUID.fromString(id));
    }
}
