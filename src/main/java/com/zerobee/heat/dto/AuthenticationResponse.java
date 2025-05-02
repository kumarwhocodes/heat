package com.zerobee.heat.dto;


import com.zerobee.heat.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class AuthenticationResponse {
    private String name;
    private Set<Role> roles;
    private String token;
}


