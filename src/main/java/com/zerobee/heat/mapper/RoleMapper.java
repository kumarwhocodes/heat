package com.zerobee.heat.mapper;

import com.zerobee.heat.dto.RoleDTO;
import com.zerobee.heat.entity.Role;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    
    RoleDTO toDTO(Role role);
    
    Role toEntity(RoleDTO roleDTO);
}
