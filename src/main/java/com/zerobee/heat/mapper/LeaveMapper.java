package com.zerobee.heat.mapper;

import com.zerobee.heat.dto.CustomerDTO;
import com.zerobee.heat.dto.LeaveDTO;
import com.zerobee.heat.entity.Customer;
import com.zerobee.heat.entity.Leave;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LeaveMapper {

    @Mapping(target = "userId", source = "user.id")
    LeaveDTO toDTO(Leave leave);

    Leave toEntity(LeaveDTO leaveDTO);

}