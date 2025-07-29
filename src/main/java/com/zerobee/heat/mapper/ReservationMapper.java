package com.zerobee.heat.mapper;

import com.zerobee.heat.dto.ReservationDTO;
import com.zerobee.heat.entity.Reservation;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {FileMapper.class})
public interface ReservationMapper {

    Reservation toEntity(ReservationDTO reservationDTO);
    ReservationDTO toDTO(Reservation reservation);

}
