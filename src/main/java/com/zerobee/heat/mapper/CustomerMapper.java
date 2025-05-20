package com.zerobee.heat.mapper;

import com.zerobee.heat.dto.CustomerDTO;
import com.zerobee.heat.dto.DayWiseDto;
import com.zerobee.heat.dto.ItineraryDto;
import com.zerobee.heat.entity.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",uses = ItineraryMapper.class)
public interface CustomerMapper {

    CustomerDTO toDTO(Customer customer);

    Customer toEntity(CustomerDTO customerDTO);

}