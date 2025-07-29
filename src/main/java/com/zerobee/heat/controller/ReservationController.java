package com.zerobee.heat.controller;

import com.zerobee.heat.dto.CustomResponse;
import com.zerobee.heat.dto.ReservationDTO;
import com.zerobee.heat.entity.Reservation;
import com.zerobee.heat.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/reservation")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping("/")
    public CustomResponse<List<ReservationDTO>> getAllReservations() {
        return new CustomResponse<>(
                HttpStatus.OK,
                "Fetched All Reservation",
                reservationService.getAllReservations()
        );
    }

    @GetMapping("/{id}")
    public CustomResponse<ReservationDTO> getReservationById(@PathVariable UUID id) {
        return new CustomResponse<>(
                HttpStatus.OK,
                "Fetched Reservation By Id: " + id,
                reservationService.getReservationById(id)
        );
    }

    @PostMapping("/")
    public CustomResponse<ReservationDTO> addReservation(@RequestBody ReservationDTO reservationDto) {
        return new CustomResponse<>(
                HttpStatus.CREATED,
                "Created Reservation",
                reservationService.addReservation(reservationDto)
        );
    }

    @PutMapping("/{id}")
    public CustomResponse<ReservationDTO> updateReservation(@RequestBody ReservationDTO reservationDto, @PathVariable UUID id) {
        return new CustomResponse<>(
                HttpStatus.OK,
                "Updated Reservation",
                reservationService.updateReservation(reservationDto,id)
        );
    }

    @DeleteMapping("/{id}")
    public CustomResponse<Void> deleteReservation(@PathVariable UUID id) {
        reservationService.deleteReservation(id);
        return new CustomResponse<>(
                HttpStatus.OK,
                "Deleted Reservation By Id: " + id,
                null
        );
    }

}
