package com.zerobee.heat.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoleDTO {
    private Integer id;
    private String name;
    
    @JsonCreator
    public static RoleDTO fromString(String name) {
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setName(name);
        return roleDTO;
    }
}
