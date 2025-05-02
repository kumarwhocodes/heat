package com.zerobee.heat.controller;

import com.zerobee.heat.dto.AuthenticationRequest;
import com.zerobee.heat.dto.AuthenticationResponse;
import com.zerobee.heat.dto.CustomResponse;
import com.zerobee.heat.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {
    
    private final UserService userService;
    
    @PostMapping("/auth/login")
    public CustomResponse<AuthenticationResponse> authenticate(@Valid @RequestBody AuthenticationRequest request) {
        return new CustomResponse<>(
                HttpStatus.OK,
                "User authenticated!",
                userService.authenticate(request)
        );
    }
    
}