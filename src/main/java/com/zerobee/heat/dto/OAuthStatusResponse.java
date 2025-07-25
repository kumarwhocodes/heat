package com.zerobee.heat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OAuthStatusResponse {
    private boolean connected;
    private String email;
    private String connectedAt;
}
