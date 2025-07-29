package com.zerobee.heat.service;

import com.zerobee.heat.dto.ReservationDTO;
import com.zerobee.heat.entity.File;
import com.zerobee.heat.entity.Reservation;
import com.zerobee.heat.exception.ResourceNotFoundException;
import com.zerobee.heat.mapper.FileMapper;
import com.zerobee.heat.mapper.ReservationMapper;
import com.zerobee.heat.repo.FileRepo;
import com.zerobee.heat.repo.ReservationRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepo reservationRepo;
    private final ReservationMapper reservationMapper;
    private final FileRepo fileRepo;
    private final FileMapper fileMapper;

    public List<ReservationDTO> getAllReservations() {
        return reservationRepo.findAll().stream()
                .map(reservationMapper::toDTO)
                .collect(Collectors.toList());
    }

    public ReservationDTO getReservationById(UUID id) {
        Reservation reservation = reservationRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("reservation not found by id: "+id));
        return reservationMapper.toDTO(reservation);
    }

    public ReservationDTO addReservation(ReservationDTO reservationDto) {
        Reservation reservation = reservationMapper.toEntity(reservationDto);
        if(reservationDto.getFile().getId() == null) {
            reservation.setFile(null);
        } else {
            UUID fileId = reservationDto.getFile().getId();
            File file = fileRepo.findById(fileId)
                    .orElseThrow(() -> new ResourceNotFoundException("File not found by id: "+fileId));
            reservation.setFile(file);
        }
        reservationRepo.save(reservation);
        return reservationMapper.toDTO(reservation);
    }

    public ReservationDTO updateReservation(ReservationDTO reservationDto, UUID id) {
        Reservation reservation = reservationRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("reservation not found by id: "+id));
        if (reservationDto.getType() != null) reservation.setType(reservationDto.getType());
        if (reservationDto.getProviderName() != null) reservation.setProviderName(reservationDto.getProviderName());
        if (reservationDto.getProviderContact() != null) reservation.setProviderContact(reservationDto.getProviderContact());
        if (reservationDto.getReferenceNumber() != null) reservation.setReferenceNumber(reservationDto.getReferenceNumber());
        if (reservationDto.getCheckInDate() != null) reservation.setCheckInDate(reservationDto.getCheckInDate());
        if (reservationDto.getCheckOutDate() != null) reservation.setCheckOutDate(reservationDto.getCheckOutDate());
        if (reservationDto.getNumberOfGuests() != null) reservation.setNumberOfGuests(reservationDto.getNumberOfGuests());
        if (reservationDto.getPrice() != null) reservation.setPrice(reservationDto.getPrice());
        if (reservationDto.getStatus() != null) reservation.setStatus(reservationDto.getStatus());
        if (reservationDto.getFile() != null){
            File file = fileMapper.toEntity(reservationDto.getFile());
            reservation.setFile(file);
        }

        return reservationMapper.toDTO(reservationRepo.save(reservation));
    }

    @Transactional
    public void deleteReservation(UUID id) {
        if(!reservationRepo.existsById(id))
            throw new ResourceNotFoundException("reservation not found");

        reservationRepo.deleteById(id);
    }
}
