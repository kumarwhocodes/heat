package com.zerobee.heat.mapper;

import com.zerobee.heat.dto.PaymentDTO;
import com.zerobee.heat.entity.Payment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {FileMapper.class})
public interface PaymentMapper {

    PaymentDTO toDTO(Payment payment);
    Payment toEntity(PaymentDTO paymentDTO);

}