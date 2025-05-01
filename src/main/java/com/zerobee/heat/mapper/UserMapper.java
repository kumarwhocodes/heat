package com.zerobee.heat.mapper;

import com.zerobee.heat.dto.UserDTO;
import com.zerobee.heat.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {RoleMapper.class})
public interface UserMapper {
    
    UserDTO toDTO(User user);
    
    User toEntity(UserDTO userDTO);
}